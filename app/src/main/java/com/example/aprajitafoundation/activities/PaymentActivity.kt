package com.example.aprajitafoundation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.data.Constants
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize Razorpay Checkout
        Checkout.preload(applicationContext)

        // Call this function when you want to start the payment
        startPayment()
    }

    private fun startPayment() {
        val checkout = Checkout()
        //Razor pay key_id
        checkout.setKeyID(Constants.rpKeyId)

        try {
            val options = JSONObject()
            options.put("name", "Aprajita Foundation")
            options.put("description", "NGO for women and child empowerment")
            options.put("image", Constants.logoLink)
            options.put("theme.color", "#F7E94E")
            options.put("currency", "INR")
            options.put("amount", "50000") // Amount in paise (e.g., Rs 500.00)
            options.put("prefill.email", "user_email@example.com")
            options.put("prefill.contact", "user_contact")

            // Start Razorpay Checkout Activity
            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }

}