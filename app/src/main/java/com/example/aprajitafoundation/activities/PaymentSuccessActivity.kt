package com.example.aprajitafoundation.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityPaymentSuccessBinding
import com.example.aprajitafoundation.model.Payment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentSuccessActivity : AppCompatActivity() {

    private var paymentDetails: Payment? = null
    private lateinit var binding: ActivityPaymentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        paymentDetails = intent.getParcelableExtra("transaction_detail")

        paymentDetails?.let {
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val date = dateFormat.format(it.date) // Assuming `date` is a field in `Payment`

            binding.tvCustomerName.text = "Customer Name: ${it.name}"
            binding.tvPaymentDate.text = "Date: $date"
            binding.tvCustomerPhone.text = "Phone No.: ${it.phone}"
            binding.tvTransactionId.text = "Transaction ID: ${it.razorpay_payment_id}"

            binding.btnDownloadReceipt.setOnClickListener {
                //downloadReceipt()
            }

            binding.btnShareReceipt.setOnClickListener {
                //shareReceipt()
            }
        }
    }


}
