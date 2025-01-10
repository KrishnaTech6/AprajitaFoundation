package com.example.aprajitafoundation.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityUpiPaymentBinding
import com.example.aprajitafoundation.utility.AnimationUtils
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.saveInputToPreferences
import com.example.aprajitafoundation.utility.showSnackBar
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import dev.shreyaspatil.easyupipayment.model.TransactionStatus
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PublicKey
import javax.crypto.Cipher

class UpiPaymentActivity: BaseActivity(){
    private lateinit var binding: ActivityUpiPaymentBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val upiPayment = 0

    private val upiId = Constants.upiId
    private val upiName = Constants.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityUpiPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //to hide status_bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            //for lower version of Android
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        AnimationUtils.fadeIn(binding.organizationImage, 2000)
        AnimationUtils.fadeIn(binding.payButton, 2000)
        //Shared preference
        sharedPreferences = getSharedPreferences(getString(R.string.apppreferences), MODE_PRIVATE)

        // Retrieve and set values to EditText
        binding.userName.setText(getInputFromPreferences(getString(R.string.name_payment)))
        //binding.userEmail.setText(getInputFromPreferences(getString(R.string.email_payment)))
        binding.userContact.setText(getInputFromPreferences(getString(R.string.phone_payment)))

        // RadioGroup listener for predefined amounts
        binding.amountToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedAmount = when (checkedId) {
                R.id.amount_100 -> 100
                R.id.amount_200 -> 200
                R.id.amount_500 -> 500
                R.id.amount_1000 -> 1000
                R.id.amount_2000 -> 2000
                else -> 0
            }
            binding.paymentAmount.setText(selectedAmount.toString())
        }

        // Call this function when you want to start the payment
        binding.payButton.setOnClickListener{
            val amount ="%.2f".format(binding.paymentAmount.text.toString().toFloat())
            if (isDetailsFilled()){
                payUsingUpi(
                    amount = amount,
                    upiId = upiId,
                    name = upiName,
                    note= "gift"
                )
            }
        }

        binding.userName.afterTextChanged { text ->
            saveInputToPreferences(this, getString(R.string.name_payment), text)
        }

//        binding.userEmail.afterTextChanged { text ->
//            saveInputToPreferences(this, getString(R.string.email_payment), text)
//        }

        binding.userContact.afterTextChanged { text ->
            saveInputToPreferences(this, getString(R.string.phone_payment), text)
        }
        binding.backArrow.setOnClickListener{
            onBackPressed()
        }
    }

    private fun isDetailsFilled(): Boolean {
        val phonePattern = Regex("^[0-9]{10}$")

        return when{
            binding.userName.text.toString().isEmpty() -> {
                showSnackBar(binding.root,getString(R.string.error_name_empty))
                false
            }
            binding.userContact.text.toString().isEmpty() ->{
                showSnackBar(binding.root,getString(R.string.enter_the_phone_number))
                false
            }
            !binding.userContact.text.toString().matches(phonePattern) -> {
                showSnackBar(binding.root,getString(R.string.enter_a_valid_10_digit_phone_number))
                false
            }
            binding.paymentAmount.text.toString().toInt() <= 0  -> {
                showSnackBar(binding.root, getString(R.string.enter_valid_amount))
                false
            }
            binding.paymentAmount.text.toString().isEmpty() -> {
                showSnackBar(binding.root, getString(R.string.enter_an_amount))
                false
            }
            else -> true
        }
    }

    private fun getInputFromPreferences(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    //payment via UPI method
    private fun payUsingUpi(amount: String, upiId: String, name: String, note: String) {

        //URL build with query and its value (CURRENCY : INR)
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()

        // Generate RSA Key Pair (public and private keys)
        val keyPair = generateRSAKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private

        // Encrypt the URI using the public key
        val encryptedUri = encryptIntentUri(uri.toString(), publicKey)


        val newUri = uri.buildUpon().appendQueryParameter("sign", encryptedUri).build()

        Log.d("TAGupi", "payUsingUpi: $newUri")


        // start payment from existing app
        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // chooser dialog to pay via all UPI app available in system
        val chooser = Intent.createChooser(upiPayIntent, getString(R.string.pay_with))

        // check if intent resolves or not
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, upiPayment)
        } else {
            Toast.makeText(
                this,
                getString(R.string.no_upi_app_found_error),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            upiPayment -> if (RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val text = data.getStringExtra("response")
                    val dataList = ArrayList<String>()
                    dataList.add(text!!)
                    upiPaymentDataOperation(dataList)
                } else {
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isInternetAvailable(this)) {
            var str: String? = data[0]
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in response.indices) {
                val equalStr =
                    response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    }

                } else {
                    paymentCancel = getString(R.string.payment_cancelled_by_user)
                }
            }

            when {
                status == "success" -> //Code to handle successful transaction here.
                    showToast( getString(R.string.payment_success))
                getString(R.string.payment_cancelled_by_user) == paymentCancel ->
                    showToast( getString(R.string.payment_cancelled_by_user))
                else ->
                    showToast( getString(R.string.transaction_failed))
            }
        } else
            showToast(getString(R.string.no_internet_available_error))

    }

    fun encryptIntentUri(uri: String, publicKey: PublicKey): String {
        return try {
            // Step 1: Hash the URI with SHA-256
            val sha256Hash = MessageDigest.getInstance("SHA-256").digest(uri.toByteArray())

            // Step 2: Encrypt the SHA-256 hash with RSA-512
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(sha256Hash)

            // Step 3: Convert encrypted bytes to Base64 string for easier storage/transmission
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Encryption failed", e)
        }
    }

    // Function to generate an RSA key pair (512 bits)
    fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(512) // Use RSA-512 key size
        return keyPairGenerator.generateKeyPair()
    }

}