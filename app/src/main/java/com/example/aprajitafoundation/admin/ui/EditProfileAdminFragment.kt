package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditAdminProfileBinding
import com.example.aprajitafoundation.model.User
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.ui.fragments.BaseFragment
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

        viewModel = ViewModelProvider(requireActivity()).get(AdminAuthViewModel::class.java)

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

        viewModel.genericResponse.observe(viewLifecycleOwner){
            if (it!=null) {
                showToast( it.message)

                hideProgressDialog()
                //fetch profile from the server after updating
                viewModel.fetchProfile(requireContext())
            }
            viewModel.resetGenericResponse()
        }


        viewModel.authResponse.observe(viewLifecycleOwner){
            if (it!=null) {
                showToast(it.message)
                // Notify activity to update header
                hideProgressDialog()
                profileUpdatedListener?.onProfileUpdated(it.user.name, it.user.email, it.user.profileImg)
            }
            viewModel.resetAuthResponse()
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
            uploadToCloudinary(requireContext(), imageUri , binding.progressBar,
                onStart = {
                    binding.btnSelectImage.isVisible= false
                }) { cloudUrl ->
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.profileImage)
                binding.btnSelectImage.isVisible= true
                user?.profileImg = cloudUrl
            }
        }
    }

    // Attach the listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProfileUpdatedListener) {
            profileUpdatedListener = context
        }
    }
}
