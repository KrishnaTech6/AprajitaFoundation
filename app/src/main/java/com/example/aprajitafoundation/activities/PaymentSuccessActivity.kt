package com.example.aprajitafoundation.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
            binding.tvTransactionId.text = "${it.razorpay_payment_id}"

            binding.btnDownloadReceipt.setOnClickListener {
                downloadReceipt()
            }

            binding.btnShareReceipt.setOnClickListener {
                shareReceipt()
            }
        }
    }

    private fun downloadReceipt() {
        // Function to capture the UI as a screenshot and save it as a document
        val rootView = binding.root
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false
        saveBitmapAsPdf(this, bitmap)
    }

    private fun shareReceipt() {
        // Function to share the receipt screenshot
        val rootView = binding.root
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        // Code to create a file and share it via Intent
        val file = saveBitmapToFile(bitmap) // Implement this function to save the bitmap to a file
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share receipt via"))
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        // Create a file in the app's cache directory
        val fileName = "Aprajita_${System.currentTimeMillis()}.png" // Use timestamp to avoid file name conflicts
        val file = File(this.cacheDir, fileName)

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            // Compress the bitmap to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            // Flush and close the stream
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Ensure the stream is closed
            outputStream?.close()
        }

        return file
    }

    private fun saveBitmapAsPdf(context: Context, bitmap: Bitmap) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        pdfDocument.finishPage(page)

        val fileName = "Aprajita_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
            outputStream?.close()
        }

        // Notify the user that the PDF has been saved
        Toast.makeText(context, "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

}
