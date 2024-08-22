package com.example.aprajitafoundation.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.DataViewModel
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.databinding.ActivityPaymentBinding
import com.example.aprajitafoundation.model.Payment
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import java.util.Date

class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener, DialogInterface.OnClickListener{
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var mProgressDialog: Dialog

    private lateinit var viewModel: DataViewModel
    private lateinit var payment:Payment
    private var orderId = ""
    private val TAG = "PaymentActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        alertDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
        alertDialogBuilder.setTitle("Payment Result")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Ok"){_,_-> }





        // Call this function when you want to start the payment
        binding.payButton.setOnClickListener{
            if (isDetailsFilled()){
                viewModel.createPayment(binding.paymentAmount.text.toString().toDouble())
            }
        }

        viewModel.loading.observe(this){
            if(it){
                showDialogProgress()
            }else{
                hideProgressDialog()
            }
        }
        viewModel.error.observe(this){
            showToast(it)
            Log.d(TAG, it)
        }

        viewModel.orderId.observe(this){
            //get and set the orderId
            orderId= it
            if (isDetailsFilled()){
                startPayment()
            }
        }


        viewModel.paymentVerificationStatus.observe(this){
            //show verified successfully msg
            showToast(it)
            //after msg go to success screen
            val intent = Intent(this@PaymentActivity, PaymentSuccessActivity::class.java)
            intent.putExtra("transaction_detail", payment)
            startActivity(intent)
        }
    }

    private fun isDetailsFilled(): Boolean {
        return when{
            binding.userContact.text.toString().isEmpty() ->{
                showToast("Enter the phone number!")
                false
            }
            binding.userEmail.text.toString().isEmpty() -> {
                showToast("Enter your email!")
                false
            }
            binding.userName.text.toString().isEmpty() -> {
                showToast("Enter your name!")
                false
            }
            binding.paymentAmount.text.toString().isEmpty() -> {
                showToast("Enter an amount!")
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
            options.put("description", "NGO for women and child empowerment")
            options.put("image", Constants.logoLink)
            options.put("theme.color", getColorHex(this, R.color.yellow))
            options.put("currency", "INR")
            options.put("order_id", orderId)
            options.put("amount", (binding.paymentAmount.text.toString().toInt() * 100).toString()) // Amount in paise (e.g., Rs 500.00)
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
                showToast("PaymentData is null. Please contact support.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("An error occurred during payment processing.")
        }
    }


    override fun onPaymentError(errorCode: Int, errorMessage: String?, paymentData: PaymentData?) {
        try {
            val message = if (paymentData != null) {
                "Payment failed: $errorMessage. Payment Data: ${paymentData.data}"
            } else {
                "Payment failed: $errorMessage. No payment data available."
            }
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.show()
            Log.e(TAG, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try{
            alertDialogBuilder.setMessage("External wallet was selected : Payment Data: ${p1?.data}")
            alertDialogBuilder.show()
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        finish()
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showDialogProgress(){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.progress_bar)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog() = mProgressDialog.dismiss()

}