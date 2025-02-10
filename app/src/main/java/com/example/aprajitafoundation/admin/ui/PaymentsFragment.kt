package com.example.aprajitafoundation.admin.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.databinding.DialogFilterBinding
import com.example.aprajitafoundation.databinding.FragmentPaymentsBinding
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.ui.fragments.BaseFragment
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.utility.showSnackBar
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.aprajitafoundation.R

class PaymentsFragment : BaseFragment() {

    private lateinit var binding: FragmentPaymentsBinding
    private var paymentsList: List<Payment> = listOf()
    private var parentPaymentsList: List<Payment> = listOf()
    private lateinit var viewModel: DataViewModel
    private var isFiltered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPaymentsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)

        setupObservers()
        setupSearchBar()
        setupDownloadPayments()

        binding.ivFilter.setOnClickListener {
            if(isFiltered) {
                binding.ivFilter.setImageResource(R.drawable.filter_icon)
                populateTable(parentPaymentsList)
                isFiltered= false
            } else showFilterDialog()
        }


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
                val query = newText ?: ""
                if (query.isNullOrEmpty()) {
                    paymentsList = parentPaymentsList
                } else {
                    paymentsList = parentPaymentsList.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true) ||
                        it.amount.toString().contains(query, ignoreCase = true) ||
                        it.date.toString().contains(query, ignoreCase = true)
                    }
                }
                populateTable(paymentsList, query)
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
    }

    private fun populateTable(paymentsList: List<Payment>, query: String = "") {
        val tableLayout = binding.tableLayoutPayments

        // Clear any existing rows (except the header)
        tableLayout.removeViews(1, tableLayout.childCount - 1)

        paymentsList.forEach { payment ->
            val tableRow = TableRow(requireContext())

            val nameTextView = createHighlightedTextView(payment.name, query)
            val emailTextView = createHighlightedTextView(payment.email, query)
            val amountTextView = createHighlightedTextView(payment.amount.toString(), query)
            val dateTextView = createHighlightedTextView(payment.date.toString(), query)
            val phoneTextView = createHighlightedTextView(payment.phone, query)
            val paymentIDTextView = createHighlightedTextView(payment.razorpay_payment_id.toString(), query)

            tableRow.addView(nameTextView)
            tableRow.addView(emailTextView)
            tableRow.addView(amountTextView)
            tableRow.addView(dateTextView)
            tableRow.addView(phoneTextView)
            tableRow.addView(paymentIDTextView)

            tableLayout.addView(tableRow)
        }
    }
    private fun createHighlightedTextView(text: String, query: String): TextView {
        val textView = TextView(requireContext()).apply {
            setPadding(8, 8, 8, 8)
        }

        if (query.isNotEmpty() && text.contains(query, ignoreCase = true)) {
            val startIndex = text.lowercase().indexOf(query.lowercase())
            val spannable = SpannableString(text)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), android.R.color.holo_purple)),
                startIndex, startIndex + query.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
        } else {
            textView.text = text
        }

        return textView
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
                        showToast( "Failed to save PDF: ${e.message}")
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
            showToast("Unable to open the file: ${e.message}")
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
        val table = Table(floatArrayOf(1f, 1.5f, 1f, 1.2f, 1f, 2f)).apply {
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
            showToast( "Permission Denied")
        }
    }

    // Filter payment by date
    private fun showFilterDialog() {
        val bindingDialog = DialogFilterBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Filter Payments by Date")
            .setView(bindingDialog.root)
            .setNegativeButton("Cancel", null)
            .create()

        val calendar = Calendar.getInstance()
        var startDate: String? = null
        var endDate: String? = null

        // Handle Start Date Selection
        bindingDialog.startDateEditText.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                startDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                bindingDialog.startDateEditText.setText(startDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }

        // Handle End Date Selection
        bindingDialog.endDateEditText.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                endDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                bindingDialog.endDateEditText.setText(endDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }

        // Apply Filter Button
        bindingDialog.applyFilterButton.setOnClickListener {
            if (startDate.isNullOrBlank() || endDate.isNullOrBlank()) {
                showSnackBar(bindingDialog.root, "Please select both dates!")
            } else {
                filterPaymentsByDate(startDate!!, endDate!!)
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }


    private fun filterPaymentsByDate(startDate: String, endDate: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            // Filter the payments list based on the selected date range
            paymentsList = parentPaymentsList.filter {
                val paymentDater = dateFormat.format(it.date)
                val paymentDate = dateFormat.parse(paymentDater)
                paymentDate in start..end
            }
            populateTable(paymentsList)
            isFiltered=true
            binding.ivFilter.setImageResource(R.drawable.filter_list_off)
        } catch (e: ParseException) {
            showSnackBar(binding.root, "Invalid Date Format")
        }
    }


}
