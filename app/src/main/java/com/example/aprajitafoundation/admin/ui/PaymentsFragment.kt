package com.example.aprajitafoundation.admin.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.databinding.FragmentPaymentsBinding
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import org.bouncycastle.asn1.x500.style.RFC4519Style.title

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentsFragment : Fragment() {
    private lateinit var binding: FragmentPaymentsBinding
    private  var paymentsList : List<Payment> = listOf()
    private lateinit var viewModel: DataViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPaymentsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)


        viewModel.getAllPayments(requireContext())

        viewModel.allPayments.observe(viewLifecycleOwner){
            paymentsList = it.payments
            populateTable()
        }

        viewModel.error.observe(viewLifecycleOwner){
            showToast(requireContext(), it)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) showDialogProgress(requireContext()) else hideProgressDialog()
        }

        binding.downloadPayments.setOnClickListener{
            if(checkPermissions()){
                savePdfFile()
            }else{
                requestPermissions()
            }
        }

        return binding.root
    }

    private fun populateTable() {
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

    private fun savePdfFile() {
        val fileName = "AllPayments_Aprajita.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)


            // Add title
            val title = Paragraph("Aprajita Foundation")
                .setFontSize(20f)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
            val subheading= Paragraph("An NGO for Women and Kids Empowerment")
                .setFontSize(14f)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)


            val paymentDetails= Paragraph("\n\nPayment Details")
                .setFontSize(18f)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)
            document.add(subheading)
            document.add(paymentDetails)

            // Create a table with 6 columns
            val table = Table(floatArrayOf(1f, 2f, 1f, 1f, 1f, 2f))
            table.setWidth(100f)

            // Add table headers
            table.addHeaderCell(createHeaderCell("Name"))
            table.addHeaderCell(createHeaderCell("Email"))
            table.addHeaderCell(createHeaderCell("Amount"))
            table.addHeaderCell(createHeaderCell("Date"))
            table.addHeaderCell(createHeaderCell("Phone"))
            table.addHeaderCell(createHeaderCell("PaymentID"))

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault())

            // Add rows to the table
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

            showToast(requireContext(), "PDF saved at: ${file.absolutePath}")

        } catch (e: Exception) {
            e.printStackTrace()
            showToast(requireContext(), "Failed to save PDF: ${e.message}")
        }
    }

    private fun createHeaderCell(text: String): Cell {
        return Cell().add(Paragraph(text).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
    }

    private fun createCell(text: String): Cell {
        return Cell().add(Paragraph(text)).setTextAlignment(TextAlignment.CENTER)
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePdfFile()
        } else {
            showToast(requireContext(), "Permission Denied")
        }
    }
}

