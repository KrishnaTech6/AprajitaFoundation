package com.example.aprajitafoundation.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.databinding.ActivityFullScreenImageBinding
import com.example.aprajitafoundation.model.SlideItem

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //to hide statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            //for lower version of Android
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }



        val imageData = intent.getParcelableExtra<SlideItem>("IMAGE_DATA")

        // Using Glide for image loading
        Glide.with(this)
            .load(imageData?.imageResourceId)
            .into(binding.fullScreenImageView)

        binding.titleView.text = imageData?.title

        if (imageData?.title.isNullOrBlank()){
            binding.titleView.visibility = View.GONE
        }else{
            binding.titleView.visibility = View.VISIBLE
        }


    }
}