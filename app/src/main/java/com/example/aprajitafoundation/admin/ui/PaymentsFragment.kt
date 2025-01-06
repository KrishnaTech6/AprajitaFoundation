package com.example.aprajitafoundation.admin.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TableRow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.databinding.FragmentPaymentsBinding
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.utility.handleLoadingState
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentsFragment : Fragment() {

    private lateinit var binding: FragmentPaymentsBinding
    private var paymentsList: List<Payment> = listOf()
    private var parentPaymentsList: List<Payment> = listOf()
    private lateinit var viewModel: DataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPaymentsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)

        setupObservers()
        setupSearchBar()
        setupDownloadPayments()

        return binding.root
    }

    private fun setupDownloadPayments() {
        binding.downloadPayments.setOnClickListener {
            if (checkPermissions()) {
                savePdfFile(paymentsList)
            } else {
                requestPermissions()
            }
        }
    }

    private fun setupSearchBar() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    paymentsList = parentPaymentsList
                } else {
                    paymentsList = parentPaymentsList.filter {
                        it.name.contains(newText, ignoreCase = true)
                    }
                }
                populateTable(paymentsList)
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.getAllPayments(requireContext())

        viewModel.allPayments.observe(viewLifecycleOwner) {
            paymentsList = it.payments
            parentPaymentsList = it.payments
            populateTable(paymentsList)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            showToast(requireContext(), it)
        }

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) handleLoadingState(requireContext(), requireView())
            else hideProgressDialog()
        }
    }

    private fun populateTable(paymentsList: List<Payment>) {
        val tableLayout = binding.tableLayoutPayments

        // Clear any existing rows (except the header)
        tableLayout.removeViews(1, tableLayout.childCount - 1)

        paymentsList.forEach { payment ->
            val tableRow = TableRow(requireContext())

            val nameTextView = TextView(requireContext()).apply {
                text = payment.name
                setPadding(8, 8, 8, 8)
            }

            val emailTextView = TextView(requireContext()).apply {
                text = payment.email
                setPadding(8, 8, 8, 8)
            }

            val amountTextView = TextView(requireContext()).apply {
                text = payment.amount.toString()
                setPadding(8, 8, 8, 8)
            }

            val dateTextView = TextView(requireContext()).apply {
                text = payment.date.toString()
                setPadding(8, 8, 8, 8)
            }

            val phoneTextView = TextView(requireContext()).apply {
                text = payment.phone.toString()
                setPadding(8, 8, 8, 8)
            }

            val paymentIDTextView = TextView(requireContext()).apply {
                text = payment.razorpay_payment_id.toString()
                setPadding(8, 8, 8, 8)
            }

            tableRow.addView(nameTextView)
            tableRow.addView(emailTextView)
            tableRow.addView(amountTextView)
            tableRow.addView(dateTextView)
            tableRow.addView(phoneTextView)
            tableRow.addView(paymentIDTextView)

            tableLayout.addView(tableRow)
        }
    }

    private fun savePdfFile(paymentsList: List<Payment>) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileName = "AllPayments_Aprajita.pdf"

            val pdfUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                Uri.fromFile(file)
            }
                try {
                    pdfUri?.let { uri ->
                        requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                            writePdf(outputStream, paymentsList)
                        }
                        withContext(Dispatchers.Main){
                            Snackbar
                                .make(binding.root, "PDF saved successfully.", Snackbar.LENGTH_SHORT)
                                .setAction("Open") {
                                    openFileLocation(uri)
                                }
                                .show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main){
                        showToast(requireContext(), "Failed to save PDF: ${e.message}")
                    }
                }
        }
    }
    private fun openFileLocation(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(requireContext(), "Unable to open the file: ${e.message}")
        }
    }

    private fun writePdf(outputStream: OutputStream, paymentsList: List<Payment>) {
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val image = Image(ImageDataFactory.create(Constants.logoLink))
            .setWidth(80f)
            .setHeight(80f)
            .setHorizontalAlignment(HorizontalAlignment.CENTER)

        // Add title and subheading
        val title = Paragraph("Aprajita Foundation")
            .setFontSize(20f)
            .setFontColor(ColorConstants.BLACK)
            .setTextAlignment(TextAlignment.CENTER)
        val subheading = Paragraph("An NGO for Women and Kids Empowerment")
            .setFontSize(14f)
            .setFontColor(ColorConstants.BLACK)
            .setTextAlignment(TextAlignment.CENTER)
        val paymentDetails = Paragraph("\nPayment Details")
            .setFontSize(18f)
            .setFontColor(ColorConstants.BLACK)
            .setTextAlignment(TextAlignment.CENTER)

        document.add(image)
        document.add(title)
        document.add(subheading)
        document.add(paymentDetails)

        // Create and add the table
        val table = Table(floatArrayOf(1f, 2f, 1f, 1f, 1f, 2f)).apply {
            setWidth(100f)
        }

        // Add table headers
        table.addHeaderCell(createHeaderCell("Name"))
        table.addHeaderCell(createHeaderCell("Email"))
        table.addHeaderCell(createHeaderCell("Amount"))
        table.addHeaderCell(createHeaderCell("Date"))
        table.addHeaderCell(createHeaderCell("Phone"))
        table.addHeaderCell(createHeaderCell("PaymentID"))

        // Add rows to the table
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault())
        paymentsList.forEach { payment ->
            table.addCell(createCell(payment.name))
            table.addCell(createCell(payment.email))
            table.addCell(createCell(payment.amount.toString()))

            val date = dateFormat.format(payment.date)
            table.addCell(createCell(date))

            table.addCell(createCell(payment.phone))
            table.addCell(createCell(payment.razorpay_payment_id.toString()))
        }

        document.add(table)
        document.close()
    }

    private fun createHeaderCell(text: String): Cell {
        return Cell().add(Paragraph(text).setBold())
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
    }

    private fun createCell(text: String): Cell {
        return Cell().add(Paragraph(text)).setTextAlignment(TextAlignment.CENTER)
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePdfFile(paymentsList)
        } else {
            showToast(requireContext(), "Permission Denied")
        }
    }
}
