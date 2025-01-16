package com.example.aprajitafoundation.admin.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.api.ImagesRequest
import com.example.aprajitafoundation.databinding.FragmentGallery2Binding
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.ui.fragments.BaseFragment
import com.example.aprajitafoundation.utility.*
import com.example.aprajitafoundation.viewmodel.DataViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GalleryAdminFragment : BaseFragment() {
    private var _binding: FragmentGallery2Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DataViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGallery2Binding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        // Add images button click listener
        binding.btnAddImages.setOnClickListener {
            checkStoragePermissionAndOpenGallery(isMultipleImages = true)
        }

        // Fetch all images initially
        viewModel.fetchAllGalleryImages()

        return binding.root
    }

    private fun setupRecyclerView() {
        val adapter = ImageEventAdapter(requireContext(),isAdmin = true, viewModel = viewModel)
        binding.rvGalleryAdmin.adapter = adapter
        binding.rvGalleryAdmin.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun observeViewModel() {
        viewModel.allImages.observe(viewLifecycleOwner) { images ->
            (binding.rvGalleryAdmin.adapter as? ImageEventAdapter)?.updateImages(images)
        }

        viewModel.deleteResponse.observe(viewLifecycleOwner) { response ->
            if (response!=null) {
                showToast(response.message)
                hideProgressDialog()
                viewModel.fetchAllGalleryImages() // Fetch updated images
            }
            viewModel.resetDeleteStatus()
        }

        viewModel.uploadResponse.observe(viewLifecycleOwner) {
            if (it!=null) {
                showToast(it.message)
                hideProgressDialog()
                viewModel.fetchAllGalleryImages()
            }
            viewModel.resetUploadStatus()
        }
        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) handleLoadingState(requireView())
            else hideProgressDialog()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1) { // RESULT_OK = -1
            val clipData = data?.clipData
            val uris = mutableListOf<Uri>()
            if (clipData != null) {
                // Handle multiple images
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    uris.add(imageUri)
                }
            }

            handleImageUpload(uris)
        }
    }

    private fun handleImageUpload(uris: List<Uri>) {
        Log.d("GalleryAdminFragment", "Handling image upload for URIs: $uris")
        lifecycleScope.launch(Dispatchers.IO) {
            coroutineScope {
                val uploadedUrls = uris.map { uri ->
                    async {
                        try {
                            Log.d("GalleryAdminFragment", "Uploading image: $uri")
                            suspendUploadToCloudinary(requireContext(), uri)
                        } catch (e: Exception) {
                            Log.e("GalleryUpload", "Failed to upload image: $uri", e)
                            null
                        }
                    }
                }.awaitAll().filterNotNull()

                if (uploadedUrls.isNotEmpty()) {
                    Log.d("GalleryAdminFragment", "Uploaded URLs: $uploadedUrls")
                    viewModel.uploadGalleryImages(requireContext(), ImagesRequest(uploadedUrls))
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun suspendUploadToCloudinary(context: Context, uri: Uri): String =
        suspendCoroutine { continuation ->
            uploadToCloudinary(context, uri, binding.loadingScreen,
                progressReport = {
                    binding.progressBar.progress = it
                    binding.progressText.text = "$it%"
                }
            ) { cloudUrl ->
                continuation.resume(cloudUrl)
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
