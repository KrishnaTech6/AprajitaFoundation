package com.example.aprajitafoundation.admin.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.api.ImagesRequest
import com.example.aprajitafoundation.databinding.FragmentGallery2Binding
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.utility.*
import com.example.aprajitafoundation.viewmodel.DataViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GalleryAdminFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 1

    private var _binding: FragmentGallery2Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DataViewModel
    private var isSwitched = false

    // Define a result launcher for selecting multiple images
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            Log.d("GalleryAdminFragment", "Image uris: $uris")
            uris?.let { handleImageUpload(uris) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGallery2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        binding.btnAddImages.setOnClickListener {
            handleImageSelection()
        }

        // Fetch all images initially
        viewModel.fetchAllGalleryImages()

        return root
    }

    private fun setupRecyclerView() {
        binding.rvGalleryAdmin.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun observeViewModel() {
        viewModel.allImages.observe(viewLifecycleOwner) { images ->
            val adapter =
                ImageEventAdapter(requireContext(), images, isAdmin = true, viewModel = viewModel)
            binding.rvGalleryAdmin.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        viewModel.deleteResponse.observe(viewLifecycleOwner) { response ->
            showToast(requireContext(), response.message)
            hideProgressDialog()
            viewModel.fetchAllGalleryImages() // Fetch updated images
        }

        viewModel.uploadResponse.observe(viewLifecycleOwner) {
            if (isSwitched) {
                showToast(requireContext(), it.message)
                hideProgressDialog()
                viewModel.fetchAllGalleryImages()
                isSwitched = false // Reset after upload is processed
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
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

        viewModel.error.observe(viewLifecycleOwner) {
            showToast(requireContext(), it)
        }
    }

    private fun handleImageSelection() {
        Log.d("GalleryAdminFragment", "select button clicked ")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GalleryAdminFragment", "permission requested")
            requestPermission()
        } else {
            Log.d("GalleryAdminFragment", "permission already given ")
            pickImages()
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PICK_IMAGE_REQUEST
        )
    }

    private fun pickImages() {
        Log.d("GalleryAdminFragment", "pickImages called")
        pickImagesLauncher.launch("image/*")
    }

    private fun handleImageUpload(uris: List<Uri>) {
        Log.d("GalleryAdminFragment", "handleImageUpload called")
        Log.d("GalleryAdminFragment", "uris: $uris")
        lifecycleScope.launch(Dispatchers.IO) {
            coroutineScope {
                val uploadedUrls = uris.map { uri ->
                    Log.d("GalleryUpload", "Processing URI: $uri")
                    async {
                        uri.let {
                            try {
                                Log.d("GalleryUpload", "Uploading image: $it")
                                suspendUploadToCloudinary(requireContext(), it)
                            } catch (e: Exception) {
                                Log.e("GalleryUpload", "Failed to upload image: $it", e)
                                null
                            }
                        }
                    }
                }.awaitAll().filterNotNull()
                Log.d("GalleryUpload", "Uploaded URLs: $uploadedUrls")

                if (uploadedUrls.isNotEmpty()) {
                    Log.d("GalleryUpload", "All images uploaded successfully")
                    viewModel.uploadGalleryImages(requireContext(), ImagesRequest(uploadedUrls))
                    isSwitched = true
                }

            }

        }
    }

    private suspend fun suspendUploadToCloudinary(context: Context, uri: Uri): String =
        suspendCoroutine { continuation ->
            uploadToCloudinary(context, uri, binding.progressBar) { cloudUrl ->
                Log.d("GalleryUpload", "Image uploaded to Cloudinary: $cloudUrl")
                continuation.resume(cloudUrl)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PICK_IMAGE_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("GalleryAdminFragment", "permission granted")
            pickImages()
        } else {
            Log.d("GalleryAdminFragment", "permission denied")
            showSnackBar(binding.root, getString(R.string.permission_denied))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
