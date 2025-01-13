package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditEventBinding
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.ui.fragments.BaseFragment
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.DataViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditEventFragment : BaseFragment() {
    //add Event and Edit Event Screen

    private lateinit var binding: FragmentEditEventBinding

    private lateinit var viewModel: DataViewModel

    private var eventModel: EventModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditEventBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]

        // Retrieve the passed event data
        eventModel = arguments?.getParcelable(getString(R.string.event_parcelable))

        // Set default title
        val isEditing = eventModel != null
        (activity as AppCompatActivity).supportActionBar?.title =
            if (isEditing) "Edit Team Member" else "Add Team Member"

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

            binding.editEventImage.setOnClickListener{
                if(!eventModel?.image.isNullOrBlank()){
                    val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
                    intent.putExtra(getString(R.string.image_url_bundle), eventModel?.image)
                    requireActivity().startActivity(intent)
                }
            }
        }

        // Initialise with default values in add member screen
        if (!isEditing) eventModel = EventModel("", "", "", Date(), "", "")

        viewModel.updateResponse.observe(viewLifecycleOwner) {
            showToast( it.message)
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        viewModel.uploadResponse.observe(viewLifecycleOwner) {
            showToast( it.message)
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }
        binding.btnSelectImage.setOnClickListener {
            checkStoragePermissionAndOpenGallery()
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
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        return binding.root
    }

    private fun isDetailsValid(): Boolean {
        return when {
            binding.editEventTitle.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_title_empty))
                false
            }

            binding.editEventDescription.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_description_empty))
                false
            }

            binding.editEventLocation.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_location_empty))
                false
            }

            binding.editEventDate.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_date_empty))
                false
            }

            eventModel?.image.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_select_image))
                false
            }

            else -> true
        }
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
            uploadToCloudinary(requireContext(), imageUri, binding.progressBar) { cloudUrl ->
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.editEventImage)

                eventModel?.image = cloudUrl
            }
        }
    }
}
