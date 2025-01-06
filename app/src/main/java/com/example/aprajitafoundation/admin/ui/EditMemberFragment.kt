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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditMemberBinding
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.model.MemberModel2
import com.example.aprajitafoundation.model.Socials
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.afterTextChanged
import com.example.aprajitafoundation.utility.handleLoadingState
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.google.gson.Gson

//Edit update team member
class EditMemberFragment : BaseFragment() {

    private lateinit var binding: FragmentEditMemberBinding

    private lateinit var viewModel: DataViewModel
    private var memberModel: MemberModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditMemberBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Retrieve the passed member data
        memberModel = arguments?.getParcelable(getString(R.string.member_parcelable))

        val isEditing = memberModel != null
        (activity as AppCompatActivity).supportActionBar?.title = if (isEditing) "Edit Team Member" else "Add Team Member"

        memberModel?.let { member ->
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

            binding.editMemberImage.setOnClickListener{
                if(!memberModel?.image.isNullOrBlank()){
                    val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
                    intent.putExtra(getString(R.string.image_url_bundle), memberModel?.image)
                    requireActivity().startActivity(intent)
                }
            }

        } ?: run {
            // Initialize with default values in add member screen
            memberModel = MemberModel("", "", "", "", "", "", Socials())
        }

        binding.editMemberName.afterTextChanged { text ->
            memberModel?.name = text
        }

        binding.editMemberPosition.afterTextChanged { text ->
            memberModel?.position = text
        }

        binding.editMemberDescription.afterTextChanged { text ->
            memberModel?.description = text
        }

        binding.editMemberQuote.afterTextChanged { text ->
            memberModel?.quote = text
        }

        binding.editLinkedin.afterTextChanged { text ->
            memberModel?.socials?.linkedin = text
        }

        binding.editFacebook.afterTextChanged { text ->
            memberModel?.socials?.facebook = text
        }

        binding.editInstagram.afterTextChanged { text ->
            memberModel?.socials?.instagram = text
        }

        binding.editTwitter.afterTextChanged { text ->
            memberModel?.socials?.twitter = text
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            showSnackBar(binding.root, error)
        }

        viewModel.updateResponse.observe(viewLifecycleOwner) {
            showToast(requireContext(), it.message)
            Log.d("EditMember", it.message)
            hideProgressDialog()
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        viewModel.uploadResponse.observe(viewLifecycleOwner) {
            showToast(requireContext(), it.message)
            Log.d("EditMember", it.message)
            hideProgressDialog()
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigateUp()
        }

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) handleLoadingState(requireContext(), requireView())
            else hideProgressDialog()
        }

        binding.btnSelectImage.setOnClickListener {
            checkStoragePermissionAndOpenGallery()
        }

        binding.btnSave.setOnClickListener {
            if (isDetailsValid()) {
                memberModel?.let {
                    //Converting socials object to json string
                    //as server is accepting as json string , and sending as object
                    val socialsJson = Gson().toJson(it.socials)

                    val memberModel2 = MemberModel2(
                        it.id,
                        it.name,
                        it.position,
                        it.image,
                        it.description,
                        it.quote,
                        socialsJson
                    )
                    if (isEditing) {
                        viewModel.updateTeamMember(requireContext(), memberModel2)
                    } else {
                        viewModel.addMember(requireContext(), memberModel2)
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
            binding.editMemberName.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_name_empty))
                false
            }

            binding.editMemberPosition.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_position_empty))
                false
            }

            binding.editMemberQuote.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_quote_empty))
                false
            }

            binding.editMemberDescription.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_description_empty))
                false
            }

            memberModel?.image.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_select_image))
                false
            }

            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            uploadToCloudinary(requireContext(), imageUri , binding.progressBar) { cloudUrl ->
                Glide.with(requireContext())
                    .load(cloudUrl)
                    .into(binding.editMemberImage)

                memberModel?.image = cloudUrl
            }
        }
    }
}
