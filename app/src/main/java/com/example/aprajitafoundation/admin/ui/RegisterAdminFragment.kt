package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.api.RegisterRequest
import com.example.aprajitafoundation.databinding.FragmentRegisterAdminBinding
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.AdminAuthViewModel

class RegisterAdminFragment : Fragment() {

    private lateinit var binding: FragmentRegisterAdminBinding
    private lateinit var viewModel: AdminAuthViewModel

    // Initializing RegisterRequest with empty values
    private var registerRequest: RegisterRequest = RegisterRequest("", "", "", "")

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterAdminBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        // Set click listener for image selection
        binding.btnSelectImage.setOnClickListener {
            openGallery()
        }

        binding.addAdminImage.setOnClickListener {
            if (!registerRequest.profileImg.isNullOrBlank()) {
                val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
                intent.putExtra(getString(R.string.image_url_bundle), registerRequest.profileImg)
                requireActivity().startActivity(intent)
            }
        }

        // Set text change listeners to update RegisterRequest
        binding.addEventTitle.afterTextChanged { text ->
            registerRequest.name = text
        }
        binding.addAdminEmail.afterTextChanged { text ->
            registerRequest.email = text
        }
        binding.addEventPassword.afterTextChanged { text ->
            registerRequest.password = text
        }

        // Save button click listener
        binding.btnSave.setOnClickListener {
            if (isDetailsValid()) {
                viewModel.registerAdmin(requireContext(), registerRequest)
            }
        }

        // Cancel button click listener
        binding.btnCancel.setOnClickListener {
            // Navigate up on cancel
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            showSnackBar(binding.root, error)
        }

        viewModel.genericResponse.observe(viewLifecycleOwner) {
            showToast(requireContext(), it.message)
            val navController =
                requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                showDialogProgress(requireContext())
                if (!isInternetAvailable(requireContext())) {
                    hideProgressDialog()
                    showSnackBar(binding.root, getString(R.string.no_internet_connection))
                }
            } else hideProgressDialog()
        }
    }

    private fun isDetailsValid(): Boolean {
        return when {
            binding.addEventTitle.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_name_empty))
                false
            }

            binding.addAdminEmail.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_name_empty))
                false
            }

            binding.addEventPassword.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_password_empty))
                false
            }

            registerRequest.profileImg.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_select_image))
                false
            }

            else -> true
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val filePath = getRealPathFromURI(imageUri)
            uploadToCloudinary(requireContext(), filePath ?: "") { cloudUrl ->
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.addAdminImage)

                registerRequest.profileImg = cloudUrl
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
