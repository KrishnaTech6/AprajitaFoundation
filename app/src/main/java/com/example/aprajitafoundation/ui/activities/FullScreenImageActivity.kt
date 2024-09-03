package com.example.aprajitafoundation.ui.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.aprajitafoundation.databinding.ActivityFullScreenImageBinding
import com.google.android.material.snackbar.Snackbar

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // To hide status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            // For lower versions of Android
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        imageUrl = intent.getStringExtra("image_url")
        Log.d("FullScreenImage", imageUrl.toString())

        // Using Glide for image loading
        Glide.with(this)
            .load(imageUrl)
            .into(binding.fullScreenImageView)

        // Set up listeners for share and download buttons
        binding.shareButton.setOnClickListener {
            imageUrl?.let { url -> shareImage(url) }
        }

        binding.downloadButton.setOnClickListener {
            imageUrl?.let { url -> downloadImageToGallery( url) }
        }
    }

    fun downloadImageToGallery(imageUrl: String) {
        Glide.with(this@FullScreenImageActivity)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImageToGallery( resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle when the load is cleared
                }
            })
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "Image_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                showDownloadSnackbar(it)
            } ?: run {
                Toast.makeText(this@FullScreenImageActivity, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Function to share the image
    private fun shareImage(url: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share image via"))
    }

    // Show Snackbar to open the image
    private fun showDownloadSnackbar(uri: Uri) {
        Snackbar.make(binding.root, "Image downloaded", Snackbar.LENGTH_LONG)
            .setAction("Open") {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "image/jpeg")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }
            .show()
    }
}
