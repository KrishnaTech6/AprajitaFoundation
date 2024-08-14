package com.example.aprajitafoundation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.data.Constants
import java.io.File
import java.io.IOException

open class BaseFragment : Fragment() {

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
            Toast.makeText(requireContext(), "No application available to open the link", Toast.LENGTH_SHORT).show()
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
             showToast("Unable to share download link")
         }

//            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
//            val apkFile = File(packageInfo.applicationInfo.sourceDir)
//
//            val uri = FileProvider.getUriForFile(
//                requireContext(),
//                "${requireContext().packageName}.provider",
//                apkFile
//            )
//
//            val intent = Intent(Intent.ACTION_SEND).apply {
//                type = "application/vnd.android.package-archive"
//                putExtra(Intent.EXTRA_STREAM, uri)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            try {
//                startActivity(Intent.createChooser(intent, "Share APK"))
//            } catch (e: Exception) {
//                Log.e("ShareError", "Error sharing APK", e)
//                showToast("Unable to share APK")
//            }

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

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

//    private fun copyApkToExternalStorage(): File? {
//        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
//        val apkFile = File(packageInfo.applicationInfo.sourceDir)
//        val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val copiedFile = File(externalDir, "AprajitaFoundation.apk")
//
//        return try {
//            apkFile.copyTo(copiedFile, overwrite = true)
//            copiedFile
//        } catch (e: IOException) {
//            Log.e("FileError", "Error copying APK file", e)
//            null
//        }
//    }
}
