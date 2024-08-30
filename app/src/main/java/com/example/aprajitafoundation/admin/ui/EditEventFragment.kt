package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditEventBinding
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.onTextChanged
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditEventFragment : Fragment() {

    private lateinit var binding:FragmentEditEventBinding
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var viewModel:DataViewModel

    private lateinit var eventModel: EventModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditEventBinding.inflate(layoutInflater)

        viewModel= ViewModelProvider(requireActivity())[DataViewModel::class.java]

        // Retrieve the passed event data
        val event = arguments?.getParcelable<EventModel>("event")
        Log.d("EditEventFargment", event.toString())

        if(event!=null){

            eventModel= event

            binding.editEventTitle.setText(event.title)
            binding.editEventDescription.setText(event.description)
            binding.editEventLocation.setText(event.location)
            Glide.with(requireContext())
                .load(event.image)
                .thumbnail(0.1f)
                .into(binding.editEventImage)

            val formattedDate = SimpleDateFormat("dd//MM/yyyy", Locale.getDefault())
            val date  =formattedDate.format(event.date)
            binding.editEventDate.setText(date)

        }

        viewModel.error.observe(viewLifecycleOwner){error->
            showSnackBar(binding.root, error)
        }
        viewModel.updateResponse.observe(viewLifecycleOwner){
            //If event update is successful
            showToast(requireContext(), it.message)
        }
        viewModel.loading.observe(viewLifecycleOwner){
            if(it) {
                showDialogProgress(requireContext())
                if(!isInternetAvailable(requireContext())){
                    hideProgressDialog()
                    showSnackBar(binding.root, "No Internet Connection!")
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            openGallery()
        }


        // Set up the click listener for the date field
        binding.editEventDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.editEventTitle.onTextChanged { text ->
            eventModel.title = text

        }
        binding.editEventLocation.onTextChanged { text ->
            eventModel.location = text

        }

        binding.editEventDescription.onTextChanged { text ->
            eventModel.description = text

        }


        binding.btnSave.setOnClickListener{
            if(isDetailsValid()){
                viewModel.updateEvent(requireContext(), eventModel)
            }
        }

        binding.btnCancel.setOnClickListener{
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        return binding.root
    }

    private fun isDetailsValid(): Boolean {

        return when {
            binding.editEventTitle.text.isNullOrBlank() ->{
                showSnackBar(binding.root, "Title can't be empty!")
                false
            }
            binding.editEventDescription.text.isNullOrBlank() ->{
                showSnackBar(binding.root, "Description can't be empty!")
                false
            }
            binding.editEventLocation.text.isNullOrBlank() ->{
                showSnackBar(binding.root, "Location can't be empty!")
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
                // Format the date as needed and set it to the TextInputEditText
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

            // Display the selected image in an ImageView (optional)
            Glide.with(requireContext())
                .load(imageUri)
                .into(binding.editEventImage)

            //eventModel.image = getImageUrlFromCloudinary(imageUri)
        }
    }

//    private fun getImageUrlFromCloudinary(imageUri: Uri): String {}


}
