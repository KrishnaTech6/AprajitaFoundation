package com.example.aprajitafoundation.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.databinding.ActivityPaymentBinding
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener, ExternalWalletListener, DialogInterface.OnClickListener{
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private var amount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Razorpay Checkout
        Checkout.preload(applicationContext)


        alertDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
        alertDialogBuilder.setTitle("Payment Result")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Ok"){_,_-> }





        // Call this function when you want to start the payment
        binding.payButton.setOnClickListener{
            if (binding.paymentAmount.text.toString().isNotEmpty()){
                startPayment()
            }else{
                Toast.makeText(this, "Enter an amount!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        //Razor pay key_id
        checkout.setKeyID(Constants.rpKeyId)

        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", "NGO for women and child empowerment")
            options.put("image", Constants.logoLink)
            options.put("theme.color", getColorHex(this, R.color.yellow))
            options.put("currency", "INR")
            options.put("amount", "${binding.paymentAmount.text}00") // Amount in paise (e.g., Rs 500.00)
            options.put("prefill.email", "user_email@example.com")
            options.put("prefill.contact", "user_contact")

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

    override fun onPaymentSuccess(p0: String?) {
        try{
            val intent = Intent(this@PaymentActivity, PaymentSuccessActivity::class.java)
            intent.putExtra("transaction_id", p0)
            startActivity(intent)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        try {
            alertDialogBuilder.setMessage("Payment Failed")
            alertDialogBuilder.show()
        }catch (e: java.lang.Exception){
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

}