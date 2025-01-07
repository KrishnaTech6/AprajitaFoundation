package com.example.aprajitafoundation.ui.fragments

import android.Manifest
import android.R.color.transparent
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showSnackBar
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private var mProgressDialog: Dialog? = null

    fun showDialogProgress(){
        if (mProgressDialog == null) {
            mProgressDialog = Dialog(requireContext()).apply {
                setContentView(R.layout.progress_bar) // Your custom layout
                setCancelable(false) // Optional: make it non-cancelable
                setCanceledOnTouchOutside(false) // Optional: prevent closing on touch outside
                window?.setBackgroundDrawableResource(transparent) // Transparent background
            }
        }
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
    }

    fun handleLoadingState( view: View) {
        if (isInternetAvailable(requireContext())) {
            showDialogProgress()
        } else {
            hideProgressDialog()
            showSnackBar(view, requireContext().getString(R.string.no_internet_connection))
        }
    }

    fun hideProgressDialog() {
        mProgressDialog?.takeIf { it.isShowing }?.dismiss()
    }

    fun openWhatsApp(phoneNumber: String) {
        val uri = Uri.parse("https://wa.me/$phoneNumber")
        val packageNames = listOf("com.whatsapp", "com.whatsapp.w4b")

        if (packageNames.any { startIntentWithPackage(uri, it) }) return
        showToast("WhatsApp is not installed")
    }

    fun openWebsite(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        // Check if there is an application that can handle the intent
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Handle the case where no browser is available
            showToast( "No application available to open the link")
        }
    }

    fun shareLink(url: String) {

        // Create an intent to share the Google Drive link
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Download the APK from: $url")
        }

        // Start the activity to let the user choose how to share the link
        try {
            startActivity(Intent.createChooser(intent, "Share Download Link"))
        } catch (e: Exception) {
            Log.e("ShareError", "Error sharing link: ${e.message}", e)
            showToast( "Unable to share download link")
        }
    }

    fun openEmail(email: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            setPackage("com.google.android.gm")
        }

        // Check if there is an email client available to handle the intent
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                startActivity(Intent.createChooser(intent, "Share Download Link"))
            } catch (e: Exception) {
                Log.e("EmailError", "Error sharing link: ${e.message}", e)
                showToast( "Unable to open email")
            }
        } else {
            // Handle the case where no email clients are installed
            showToast("No email apps installed")
        }

    }

    private fun startIntentWithPackage(uri: Uri, packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(packageName)
        }
        return startActivitySafely(intent)
    }

    private fun startActivitySafely(intent: Intent): Boolean {
        return try {
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: ActivityNotFoundException) {
            Log.e("IntentError", "Activity not found for ${intent.`package`}, URI: ${intent.data}", e)
            false
        }
    }


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
