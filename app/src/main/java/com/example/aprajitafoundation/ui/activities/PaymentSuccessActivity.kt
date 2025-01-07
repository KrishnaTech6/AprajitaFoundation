package com.example.aprajitafoundation.ui.activities

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityPaymentSuccessBinding
import com.example.aprajitafoundation.model.Payment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentSuccessActivity : BaseActivity() {

    private var paymentDetails: Payment? = null
    private lateinit var binding: ActivityPaymentSuccessBinding
    private var isDownloaded=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        paymentDetails = intent.getParcelableExtra(getString(R.string.transaction_detail))

        paymentDetails?.let {
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault())
            val date = dateFormat.format(it.date) // Assuming `date` is a field in `Payment`

            binding.tvCustomerName.text = "Customer Name: ${it.name}"
            binding.tvCustomerEmail.text = "Customer Email: ${it.email}"
            binding.tvPaymentDate.text = "Date: $date"
            binding.tvCustomerPhone.text = "Phone No.: ${it.phone}"

            binding.tvTransactionId.text = "Transaction ID: ${it.razorpay_payment_id}"
            binding.tvAmount.text = "Amount: ₹${it.amount}"
            binding.tvOrderId.text = "Order ID: ${it.razorpay_order_id}"

            binding.btnDownloadReceipt.setOnClickListener {
                if (!isDownloaded)
                    downloadReceipt()
                else
                    showToast( "File already downloaded! Check Downloads")

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
            clipData = ClipData.newUri(contentResolver, "Receipt_image", uri)
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
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        val uri = Uri.fromFile(file)
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
        Snackbar
            .make(binding.root, "PDF saved successfully.", Snackbar.LENGTH_SHORT)
            .setAction("Open") {
                openFileLocation(uri)
            }
            .show()
        isDownloaded=true
    }

    fun openFileLocation(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Unable to open the file: ${e.message}")
        }
    }


}
