package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditEventBinding
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.DataViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditEventFragment : Fragment() {
    //add Event and Edit Event Screen

    private lateinit var binding: FragmentEditEventBinding
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var viewModel: DataViewModel

    private var eventModel: EventModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditEventBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Retrieve the passed event data
        eventModel = arguments?.getParcelable("event")

        // Set default title
        val isEditing = eventModel != null
        (activity as AppCompatActivity).supportActionBar?.title = if (isEditing) "Edit Team Member" else "Add Team Member"

        eventModel?.let { event ->
            binding.editEventTitle.setText(event.title)
            binding.editEventDescription.setText(event.description)
            binding.editEventLocation.setText(event.location)
            Glide.with(requireContext())
                .load(event.image)
                .thumbnail(0.1f)
                .into(binding.editEventImage)

            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formattedDate.format(event.date)
            binding.editEventDate.setText(date)
        }

        // Initialise with default values in add member screen
        if(!isEditing) eventModel= EventModel("", "", "",  Date(), "", "")

        viewModel.error.observe(viewLifecycleOwner) { error ->
            showSnackBar(binding.root, error)
        }

        viewModel.updateResponse.observe(viewLifecycleOwner) {
            showToast(requireContext(), it.message)
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        viewModel.uploadResponse.observe(viewLifecycleOwner){
            showToast(requireContext(), it.message)
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                showDialogProgress(requireContext())
                if (!isInternetAvailable(requireContext())) {
                    hideProgressDialog()
                    showSnackBar(binding.root, "No Internet Connection!")
                }
            } else hideProgressDialog()
        }


        binding.btnSelectImage.setOnClickListener {
            openGallery()
        }

        binding.editEventDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.editEventTitle.afterTextChanged { text ->
            eventModel?.title = text
        }
        binding.editEventLocation.afterTextChanged { text ->
            eventModel?.location = text
        }
        binding.editEventDescription.afterTextChanged { text ->
            eventModel?.description = text
        }

        val editTexts = arrayOf(
            binding.editEventTitle,
            binding.editEventDescription,
            binding.editEventLocation,
            binding.editEventDate
        )

        editTexts.forEach { editText ->
            editText.setOnClickListener {
                editText.selectAll()
            }
        }


        binding.btnSave.setOnClickListener {

            Log.d("Event", "$eventModel")
            if (isDetailsValid()) {
                if (isEditing) {
                    eventModel?.let { viewModel.updateEvent(requireContext(), it) }
                } else {
                    eventModel?.let {
                        viewModel.addEvent(requireContext(), it)
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        return binding.root
    }

    private fun isDetailsValid(): Boolean {
        return when {
            binding.editEventTitle.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Title can't be empty!")
                false
            }
            binding.editEventDescription.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Description can't be empty!")
                false
            }
            binding.editEventLocation.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Location can't be empty!")
                false
            }
            binding.editEventDate.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Date can't be empty!")
                false
            }
            eventModel?.image.isNullOrBlank() ->{
                showSnackBar(binding.root, "Please select an image!")
                false
            }
            else -> true
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }

                eventModel?.date = selectedCalendar.time
                val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                binding.editEventDate.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val filePath = getRealPathFromURI(imageUri)
            uploadToCloudinary(requireContext(), filePath ?: "") { cloudUrl ->
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.editEventImage)

                eventModel?.image = cloudUrl
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(contentUri, proj, null, null, null)
        cursor?.use {
            val column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(column_index)
            }
        }
        return null
    }
}
