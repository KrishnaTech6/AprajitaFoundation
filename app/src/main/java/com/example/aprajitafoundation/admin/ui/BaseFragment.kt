package com.example.aprajitafoundation.admin.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.utility.showSnackBar

open class BaseFragment : Fragment() {
    protected val PICK_IMAGE_REQUEST = 1

    // Register permission launcher for storage permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openGallery(isMultipleImages)
            } else {
                showSnackBar(
                    requireView(),
                    "Permission denied.\nGo to app> settings> allow permissions"
                )
            }
        }

    // Flag to determine if multiple images should be selected
    private var isMultipleImages: Boolean = false

    // Check for storage permission based on Android version and open the gallery
    protected fun checkStoragePermissionAndOpenGallery(isMultipleImages: Boolean = false) {
        this.isMultipleImages = isMultipleImages // Set the flag

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // On Android 10+ (API 29), we don't need to request storage permission.
                openGallery(isMultipleImages)
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // If permission is already granted
                openGallery(isMultipleImages)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // Show rationale if the permission was previously denied
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to access your photos.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                // Request permission directly if it's not already granted or denied with "Don't ask again"
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    // Open the gallery to pick images, handling scoped storage for Android 10+ devices
    private fun openGallery(isMultipleImages: Boolean) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 (API 29) and above, use ACTION_GET_CONTENT for multiple image selection
            Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                if (isMultipleImages) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
        } else {
            // For older versions, use ACTION_PICK
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                if (isMultipleImages) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
}
