package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.content.Context
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
import com.example.aprajitafoundation.api.User
import com.example.aprajitafoundation.databinding.FragmentEditAdminProfileBinding
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.AdminAuthViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditProfileAdminFragment : BaseFragment() {

    private lateinit var binding: FragmentEditAdminProfileBinding
    private lateinit var viewModel: AdminAuthViewModel
    private var user: User? = null

    // Define an interface for communication
    interface OnProfileUpdatedListener {
        fun onProfileUpdated(name: String, email: String, profileImg: String?)
    }

    private var profileUpdatedListener: OnProfileUpdatedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditAdminProfileBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(AdminAuthViewModel::class.java)

        // Retrieve user data from shared preferences
        val gson = Gson()
        val sharedPreferences = requireContext().getSharedPreferences(getString(R.string.apppreferences), Activity.MODE_PRIVATE)
        val savedUserJson = sharedPreferences.getString(getString(R.string.user_data_admin), "")
        val type = object : TypeToken<User>() {}.type
        user = gson.fromJson(savedUserJson, type)

        // Display user data
        user?.let {
            binding.editName.setText(it.name)
            binding.editEmail.setText(it.email)
            Glide.with(requireContext())
                .load(it.profileImg)
                .into(binding.profileImage)
            binding.profileImage.setOnClickListener{
                if(!user?.profileImg.isNullOrBlank()){
                    val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
                    intent.putExtra(getString(R.string.image_url_bundle), user?.profileImg)
                    requireActivity().startActivity(intent)
                }
            }
        }?: run { user = User("", "", "", "") }

        // Handle select image button click
        binding.btnSelectImage.setOnClickListener {
            checkStoragePermissionAndOpenGallery()
        }

        // Handle save button click
        binding.btnSave.setOnClickListener {
            val name = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()

            if (validateInputs(name, email)) {
                user?.let {
                    it.name = name
                    it.email = email

                    // Update user in ViewModel
                    viewModel.updateProfile(requireContext(), it)
                }
            }
        }

        // Handle cancel button click
        binding.btnCancel.setOnClickListener {
            // Navigate up on cancel
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }


        viewModel.error.observe(viewLifecycleOwner){
            showSnackBar(binding.root, it)
        }

        viewModel.genericResponse.observe(viewLifecycleOwner){
            showToast(requireContext(), it.message)

            hideProgressDialog()
            //fetch profile from the server after updating
            viewModel.fetchProfile(requireContext())
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

        viewModel.authResponse.observe(viewLifecycleOwner){
            showToast(requireContext(), it.message)
            // Notify activity to update header
            hideProgressDialog()
            profileUpdatedListener?.onProfileUpdated(it.user.name, it.user.email, it.user.profileImg)
        }

        return binding.root
    }

    private fun validateInputs(name: String, email: String): Boolean {
        return when {
            name.isBlank() -> {
                binding.editName.error = getString(R.string.error_name_empty)
                false
            }
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.editEmail.error = getString(R.string.error_invalid_email_address)
                false
            }
            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            val imageUri: Uri = data.data!!
            val filePath = getRealPathFromURI(imageUri)
            uploadToCloudinary(requireContext(), filePath ?: "" , binding.progressBar) { cloudUrl ->
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.profileImage)
                user?.profileImg = cloudUrl
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(contentUri, proj, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }

    // Attach the listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProfileUpdatedListener) {
            profileUpdatedListener = context
        }
    }
}
