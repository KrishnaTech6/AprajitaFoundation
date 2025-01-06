package com.example.aprajitafoundation.ui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.databinding.ActivityPaymentBinding
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.handleLoadingState
import com.example.aprajitafoundation.utility.saveInputToPreferences
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONException
import org.json.JSONObject
import java.util.Date

class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener, DialogInterface.OnClickListener{
    private lateinit var binding: ActivityPaymentBinding

    private lateinit var viewModel: DataViewModel
    private lateinit var payment:Payment
    private var orderId = ""
    private val TAG = "PaymentActivity"

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //to hide statusbar
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

        //Shared preference
        sharedPreferences = getSharedPreferences(getString(R.string.apppreferences), MODE_PRIVATE)

        // Retrieve and set values to EditText
        binding.userName.setText(getInputFromPreferences(getString(R.string.phone_payment)))
        binding.userEmail.setText(getInputFromPreferences(getString(R.string.email_payment)))
        binding.userContact.setText(getInputFromPreferences(getString(R.string.phone_payment)))

        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        // Initialize Razorpay Checkout
        Checkout.preload(applicationContext)

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
            if (isDetailsFilled()){
                viewModel.createPayment(binding.paymentAmount.text.toString().toDouble())
                //Now we wait for the order_id to be recieved from server
                //go to viewmodel.orderId observer
            }
        }

        // Observe the loading LiveData
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) handleLoadingState(this,binding.root)
            else hideProgressDialog()
        }

        viewModel.error.observe(this){
            showToast(this, it)
            Log.d(TAG, it)
        }

        viewModel.orderId.observe(this){
            //get and set the orderId from server

            orderId= it // will be used inside startPayment()

            if (isDetailsFilled()){
                startPayment()
            }
        }


        viewModel.paymentVerificationStatus.observe(this){
            //show verified successfully msg
            showToast(this, it)
            //after msg go to success screen
            val intent = Intent(this@PaymentActivity, PaymentSuccessActivity::class.java)
            intent.putExtra(getString(R.string.transaction_detail), payment)
            startActivity(intent)
        }

        binding.userName.afterTextChanged { text ->
            saveInputToPreferences(this@PaymentActivity, getString(R.string.name_payment), text)
        }

        binding.userEmail.afterTextChanged { text ->
            saveInputToPreferences(this@PaymentActivity, getString(R.string.email_payment), text)
        }

        binding.userContact.afterTextChanged { text ->
            saveInputToPreferences(this@PaymentActivity, getString(R.string.phone_payment), text)
        }
    }

    private fun isDetailsFilled(): Boolean {

        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        val phonePattern = Regex("^[0-9]{10}$")

        return when{
            binding.userName.text.toString().isEmpty() -> {
                showSnackBar(binding.root,getString(R.string.error_name_empty))
                false
            }
            binding.userEmail.text.toString().isEmpty() -> {
                showSnackBar(binding.root,getString(R.string.error_email_empty))
                false
            }
            !binding.userEmail.text.toString().matches(emailPattern) -> {
                showSnackBar(binding.root,getString(R.string.error_invalid_email_address))
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
            binding.paymentAmount.text.toString().isEmpty() -> {
                showSnackBar(binding.root, getString(R.string.enter_an_amount))
                false
            }
            else -> true
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        //Razor pay key_id
        checkout.setKeyID(Constants.rpKeyId)

        Log.d("PaymentActivity", orderId)

        try {
            val options = JSONObject()

            options.put("name", getString(R.string.app_name))
            options.put("description", getString(R.string.aprajita_subheading))
            options.put("image", Constants.logoLink)
            options.put("theme.color", getColorHex(this, R.color.yellow))
            options.put("currency", "INR")
            options.put("order_id", orderId)
            options.put("amount", (binding.paymentAmount.text.toString().toDouble() * 100).toString()) // Amount in paise (e.g., Rs 500.00)
            options.put("prefill.email", binding.userEmail.text.toString())
            options.put("prefill.contact", binding.userContact.text.toString())

            // Start Razorpay Checkout Activity
            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getColorHex(context: Context, colorResId: Int): String {
        val colorInt = ContextCompat.getColor(context, colorResId)
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        try {
            // Check if paymentData is null
            if (paymentData != null) {
                // Extract the Razorpay signature, order ID, and payment ID
                val razorpaySignature = paymentData.signature
                val razorpayOrderId = paymentData.orderId

                Log.d(TAG, "Signature: $razorpaySignature")
                Log.d(TAG, "Order ID: $razorpayOrderId")
                Log.d(TAG, "Data: ${paymentData.data}")

                payment = Payment(
                    name = binding.userName.text.toString(),
                    email = binding.userEmail.text.toString(),
                    amount = binding.paymentAmount.text.toString().toDouble(),
                    phone = binding.userContact.text.toString(),
                    date = Date(System.currentTimeMillis()),
                    razorpay_order_id = razorpayOrderId, // Use local orderId if paymentData.orderId is null
                    razorpay_payment_id = razorpayPaymentId,
                    razorpay_signature = razorpaySignature
                )
                viewModel.verifyPayment(payment)

                Log.d(TAG, "Payment: $payment")

            } else {
                // Handle the case where paymentData is null
                Log.e(TAG, "PaymentData is null")
                showToast(this,"PaymentData is null. Please contact support.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(this, "An error occurred during payment processing.")
        }
    }


    override fun onPaymentError(errorCode: Int, errorMessage: String?, paymentData: PaymentData?) {
        try {
            val message1 = errorMessage ?: "An unknown error occurred."
            // Format the error message to show only relevant parts
            val formattedMessage = formatErrorMessage(message1)

            showFailureDialog(formattedMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to format the raw error message
    private fun formatErrorMessage(rawMessage: String): String {
        return try {
            // Parse the raw JSON message
            val jsonObject = JSONObject(rawMessage)
            val error = jsonObject.getJSONObject("error")
            val code = error.getString("code")
            val description = error.getString("description")
            val reason = error.getString("reason")

            val metadata= error.getJSONObject("metadata")

            // Return a formatted message
            "Error Code: $code\nDescription: $description\n\n Reason: $reason\n $metadata "
        } catch (e: JSONException) {
            // If there's an error in parsing, return the raw message
            rawMessage
        }
    }


    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try{
            //todo: somethihng
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        finish()
    }


    private fun getInputFromPreferences(key: String): String? {
        return sharedPreferences.getString(key, null)
    }


    private fun showFailureDialog(message1: String) {
        val dialogView = layoutInflater.inflate(R.layout.payment_failed, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()

        // Set the message and button actions
        dialogView.findViewById<TextView>(R.id.tv_error).text = message1
        dialogView.findViewById<TextView>(R.id.tv_other_error).visibility = View.GONE
        dialogView.findViewById<Button>(R.id.btn_retry).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}