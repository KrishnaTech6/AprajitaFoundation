package com.example.aprajitafoundation.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.admin.AdminActivity
import com.example.aprajitafoundation.utility.AnimationUtils
import com.example.aprajitafoundation.databinding.ActivitySplashBinding
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: DataViewModel
    private lateinit var binding:ActivitySplashBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Ensure Firebase is initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        //to hide statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        mAuth = FirebaseAuth.getInstance()

        AnimationUtils.fadeIn(binding.imageView, 1000)
        AnimationUtils.fadeIn(binding.tvLogoText, 1000)


        val sharedPreferences = getSharedPreferences(getString(R.string.apppreferences), MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean(getString(R.string.apptheme), false)

        // Check if the current mode does not match the stored theme
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val adminLogin = sharedPreferences.getString(getString(R.string.token_login_admin), "")


        if (!isInternetAvailable(this)) {
            AlertDialog.Builder(this)
                .setTitle("No Internet")
                .setMessage("Internet connection is required to use this app.")
                .setPositiveButton("Retry") { _, _ -> recreate() }
                .setNegativeButton("Exit") { _, _ -> finish() }
                .show()
        }else{
            // Fetch the gallery images and members
            viewModel.fetchTeamMembers()
            viewModel.fetchAllEvents()
            //to show splash screen for 2s and go to main screen
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if (mAuth.currentUser != null) {// Check if user is already signed in
                        gotoActivity(MainActivity::class.java)
                    }else if(!adminLogin.isNullOrBlank()){ // check if admin is already signed in
                        gotoActivity(AdminActivity::class.java)
                    }else{
                        Log.d("SplashActivity", "onCreate: ${mAuth.currentUser}")
                        gotoActivity(LoginActivity::class.java)
                    }
                }, 2500
            )
        }
    }

    private fun gotoActivity(activityClass: Class<out Activity>){
        Log.d("SplashActivity", "gotoActivity: ${activityClass.simpleName}")
        val intent=  Intent(this@SplashActivity, activityClass)
        startActivity(intent)
        finish()
    }
}