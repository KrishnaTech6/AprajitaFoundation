package com.example.aprajitafoundation.admin.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.api.ImagesRequest
import com.example.aprajitafoundation.databinding.FragmentGallery2Binding
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.utility.uploadToCloudinary
import com.example.aprajitafoundation.viewmodel.DataViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GalleryAdminFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 1

    private var _binding: FragmentGallery2Binding? = null
    private var isSwitched = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: DataViewModel

    // Define a result launcher for selecting multiple images
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris?.let { imageUris ->
                lifecycleScope.launch {
                    val imageList = mutableListOf<String>()

                    // Launch a coroutine for each image URI
                    for (uri in imageUris) {
                        val filePath = getRealPathFromURI(uri)
                        filePath?.let {
                            val cloudUrl = suspendUploadToCloudinary(requireContext(), it)
                            imageList.add(cloudUrl)
                        }
                    }
                    // Call the ViewModel function with the completed list
                    if (imageList.isNotEmpty()){
                        viewModel.uploadGalleryImages(
                            requireContext(),
                            ImagesRequest(imageList)
                        )
                        isSwitched=true
                    }

                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentGallery2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Observe the images LiveData
        viewModel.allImages.observe(viewLifecycleOwner) { images ->
            Log.d("ViewModel", "$images")
            val imageEventAdapter =
                ImageEventAdapter(requireContext(), images, isAdmin = true, viewModel = viewModel)
            binding.rvGalleryAdmin.adapter = imageEventAdapter
            imageEventAdapter.notifyDataSetChanged()
        }

        viewModel.deleteResponse.observe(viewLifecycleOwner) { response ->
                // Response after successful deletion of image
                showToast(requireContext(), response.message)
                // Now again fetch the updated list from the server
                hideProgressDialog()
                viewModel.fetchAllGalleryImages()
        }

        viewModel.uploadResponse.observe(viewLifecycleOwner) {
            if(isSwitched){
                showToast(requireContext(), it.message)
                hideProgressDialog()
                viewModel.fetchAllGalleryImages()
                isSwitched=true
            }
        }

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvGalleryAdmin.layoutManager = staggeredGridLayoutManager

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state (e.g., show/hide a ProgressBar)
            if (isLoading) {
                showDialogProgress(requireContext())
                if (!isInternetAvailable(requireContext())) {
                    hideProgressDialog()
                    showSnackBar(requireView(), getString(R.string.no_internet_connection))
                }
            } else {
                hideProgressDialog()
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            showToast(requireContext(), it)
        }

        // Fetch all images
        viewModel.fetchAllGalleryImages()

        binding.btnAddImages.setOnClickListener {
            //Pick multiple images from the gallery
            //and upload to the server
            pickImagesLauncher.launch("image/*")
        }

        return root
    }

    private suspend fun suspendUploadToCloudinary(context: Context, filePath: String): String {
        return suspendCoroutine { continuation ->
            uploadToCloudinary(context, filePath, binding.progressBar ) { cloudUrl ->
                continuation.resume(cloudUrl)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
