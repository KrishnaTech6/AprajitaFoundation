package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.databinding.FragmentPaymentsBinding
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel

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


        viewModel.getAllPayments()

        viewModel.allPayments.observe(viewLifecycleOwner){
            paymentsList = it
            populateTable()
        }

        viewModel.error.observe(viewLifecycleOwner){
            showToast(requireContext(), it)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) showDialogProgress(requireContext()) else hideProgressDialog()
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
}

