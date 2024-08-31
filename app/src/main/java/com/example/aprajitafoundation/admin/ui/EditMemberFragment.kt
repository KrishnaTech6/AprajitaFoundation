package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditMemberBinding
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.DataViewModel

class EditMemberFragment : Fragment() {

    private lateinit var binding: FragmentEditMemberBinding
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var viewModel: DataViewModel
    private lateinit var memberModel: MemberModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditMemberBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Retrieve the passed member data
        val member = arguments?.getParcelable<MemberModel>("member")
        Log.d("EditMemberFragment", member.toString())

        if (member != null) {
            memberModel = member

            // Set data to UI components
            binding.editMemberName.setText(member.name)
            binding.editMemberPosition.setText(member.position)
            binding.editMemberDescription.setText(member.description)
            binding.editMemberQuote.setText(member.quote)

            Glide.with(requireContext())
                .load(member.image)
                .thumbnail(0.1f)
                .into(binding.editMemberImage)

            binding.editLinkedin.setText(member.socials?.linkedin ?: "")
            binding.editFacebook.setText(member.socials?.facebook ?: "")
            binding.editInstagram.setText(member.socials?.instagram ?: "")
            binding.editTwitter.setText(member.socials?.twitter ?: "")
        }

        binding.editMemberName.afterTextChanged { text ->
            memberModel.name = text
        }

        binding.editMemberPosition.afterTextChanged { text ->
            memberModel.position = text
        }

        binding.editMemberDescription.afterTextChanged { text ->
            memberModel.description = text
        }

        binding.editMemberQuote.afterTextChanged { text ->
            memberModel.quote = text
        }

        binding.editLinkedin.afterTextChanged { text ->
            memberModel.socials?.linkedin = text
        }

        binding.editFacebook.afterTextChanged { text ->
            memberModel.socials?.facebook = text
        }

        binding.editInstagram.afterTextChanged { text ->
            memberModel.socials?.instagram = text
        }

        binding.editTwitter.afterTextChanged { text ->
            memberModel.socials?.twitter = text
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            showSnackBar(binding.root, error)
        }

        viewModel.updateResponse.observe(viewLifecycleOwner) {
            // If member update is successful
            showToast(requireContext(), it.message)
            Log.d("EditMember", it.message)
            // Navigate up or handle the response as needed

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

        binding.btnSave.setOnClickListener {
            if (isDetailsValid()) {
                Log.d("EditMember", "btnSave clicked")
                viewModel.updateTeamMember(requireContext(), memberModel)
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
            binding.editMemberName.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Name can't be empty!")
                false
            }

            binding.editMemberPosition.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Position can't be empty!")
                false
            }

            binding.editMemberQuote.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Quote can't be empty!")
                false
            }

            binding.editMemberDescription.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Description can't be empty!")
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
            Log.d("Cloud", imageUri.toString())

            // Get the file path from the URI
            val filePath = getRealPathFromURI(imageUri)
            Log.d("File Path", filePath ?: "Path not found")

            // Upload to cloud
            uploadToCloudinary(requireContext(), filePath ?: "") { cloudUrl ->
                // After URL received from cloud
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.editMemberImage)

                // Update member image
                memberModel.image = cloudUrl
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
