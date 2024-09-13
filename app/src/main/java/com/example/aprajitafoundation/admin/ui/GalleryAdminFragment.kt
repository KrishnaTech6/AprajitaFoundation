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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
                lifecycleScope.launch(Dispatchers.IO) {
                    val imageList = imageUris.map { uri ->
                        async {
                            val filePath = getRealPathFromURI(uri)
                            filePath?.let {
                                try {
                                    suspendUploadToCloudinary(requireContext(), it)
                                } catch (e: Exception) {
                                    Log.e("GalleryUpload", "Failed to upload image: $it", e)
                                    null // return null in case of failure
                                }
                            }
                        }
                    }.awaitAll()

                    // Filter out null results and upload only successfully uploaded URLs
                    val uploadedUrls = imageList.filterNotNull()

                    if (uploadedUrls.isNotEmpty()) {
                        // Call the ViewModel function with the completed list of URLs
                        viewModel.uploadGalleryImages(
                            requireContext(),
                            ImagesRequest(uploadedUrls)
                        )
                        isSwitched = true
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
            if (isSwitched) {
                showToast(requireContext(), it.message)
                hideProgressDialog()
                viewModel.fetchAllGalleryImages()
                isSwitched = true
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

        viewModel.error.observe(viewLifecycleOwner) {
            showToast(requireContext(), it)
        }

        // Fetch all images
        viewModel.fetchAllGalleryImages()

        binding.btnAddImages.setOnClickListener {
            //Pick multiple images from the gallery
            //and upload to the server
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PICK_IMAGE_REQUEST
                )
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    pickImagesLauncher.launch("image/*")
                } else {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(intent, PICK_IMAGE_REQUEST)
                }
            }
        }

        return root
    }

    private suspend fun suspendUploadToCloudinary(context: Context, filePath: String): String {
        return suspendCoroutine { continuation ->
            uploadToCloudinary(context, filePath, binding.progressBar) { cloudUrl ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val imageList = mutableListOf<String>()

                // Handle both single and multiple images
                val uris = getUrisFromIntent(data)

                // Use async for parallel uploading of images
                val uploadJobs = uris.map { uri ->
                    async {
                        val filePath = getRealPathFromURI(uri)
                        filePath?.let {
                            try {
                                suspendUploadToCloudinary(requireContext(), it)
                            } catch (e: Exception) {
                                Log.e("GalleryUpload", "Failed to upload image: $it", e)
                                null // return null in case of failure
                            }
                        }
                    }
                }

                // Await all image uploads and filter out any null results
                imageList.addAll(uploadJobs.awaitAll().filterNotNull())

                // Call the ViewModel function with the completed list
                if (imageList.isNotEmpty()) {
                    viewModel.uploadGalleryImages(requireContext(), ImagesRequest(imageList))
                    isSwitched = true
                }
            }
        }
    }

    private fun getUrisFromIntent(data: Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()

        // Handle multiple image selection
        data?.clipData?.let {
            for (i in 0 until it.itemCount) {
                uris.add(it.getItemAt(i).uri)
            }
        }

        // Handle single image selection
        data?.data?.let {
            uris.add(it)
        }

        return uris
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImagesLauncher.launch("image/*")
            } else {
                // Permission denied, handle accordingly (e.g., show Snackbar)
                showSnackBar(binding.root, "Permission denied to access gallery")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
