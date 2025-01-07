package com.example.aprajitafoundation.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
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

class UpiPaymentActivity: BaseActivity(), PaymentStatusListener {
    private lateinit var binding: ActivityUpiPaymentBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val upiId = Constants.upiId
    private val upiName = Constants.name
    private lateinit var easyUpiPayment: EasyUpiPayment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityUpiPaymentBinding.inflate(layoutInflater)
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
            if (isDetailsFilled()){
                pay()
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

    private fun pay() {
        val payeeVpa = upiId
        val payeeName = upiName
        val transactionId = "TID" + System.currentTimeMillis()
        val transactionRefId = "TID" + System.currentTimeMillis()
//        note - if payments are not completed leave the merchant code empty.
        val payeeMerchantCode = "demo merchant code"
        val description = "Donation by \n" +
                "Name : ${binding.userName.text.toString()}" +
                "\nPhone : ${binding.userContact.text.toString()}"
        val amount = binding.paymentAmount.text.toString()
//        val paymentAppChoice = radioAppChoice

        val paymentApp =  PaymentApp.ALL


        try {
            // START PAYMENT INITIALIZATION
            easyUpiPayment = EasyUpiPayment(this) {
                this.paymentApp = paymentApp
                this.payeeVpa = payeeVpa
                this.payeeName = payeeName
                this.transactionId = transactionId
                this.transactionRefId = transactionRefId
                this.payeeMerchantCode = payeeMerchantCode
                this.description = description
                this.amount = amount
            }
            // END INITIALIZATION

            // Register Listener for Events
            easyUpiPayment.setPaymentStatusListener(this)

            // Start payment / transaction
            easyUpiPayment.startPayment()
        }
        catch (e: Exception) {
            e.printStackTrace()
            showToast("Error: ${e.message}")
        }
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString())

        when (transactionDetails.transactionStatus) {
            TransactionStatus.SUCCESS -> onTransactionSuccess(transactionDetails)
            TransactionStatus.FAILURE -> onTransactionFailed(transactionDetails)
            TransactionStatus.SUBMITTED -> onTransactionSubmitted()
        }
    }

    override fun onTransactionCancelled() {
        // Payment Cancelled by User
        showSnackBar(binding.root, "Cancelled by user")
    }

    private fun onTransactionSuccess(transactionDetails: TransactionDetails) {
        // Payment Success
        showSnackBar(binding.root, "Success")
        Log.d("TransactionDetails", transactionDetails.toString())
    }

    private fun onTransactionSubmitted() {
        // Payment Pending
        showSnackBar(binding.root, "Pending | Submitted")
    }

    private fun onTransactionFailed(transactionDetails: TransactionDetails) {
        // Payment Failed
        showSnackBar(binding.root, "Failed")
        Log.d("TransactionDetails", transactionDetails.toString())

    }

}