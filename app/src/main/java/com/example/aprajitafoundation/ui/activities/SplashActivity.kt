package com.example.aprajitafoundation.ui.activities

import android.app.Activity
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
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.admin.AdminActivity
import com.example.aprajitafoundation.utility.AnimationUtils
import com.example.aprajitafoundation.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val theme = sharedPreferences.getString(getString(R.string.apptheme), getString(R.string.light_mode))

        // Check if the current mode does not match the stored theme
        if (theme == getString(R.string.dark_mode) && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if (theme == getString(R.string.light_mode) && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        val adminLogin = sharedPreferences.getString(getString(R.string.token_login_admin), "")


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
            }, 2000
        )
    }

    private fun gotoActivity(activityClass: Class<out Activity>){
        Log.d("SplashActivity", "gotoActivity: ${activityClass.simpleName}")
        val intent=  Intent(this@SplashActivity, activityClass)
        startActivity(intent)
        finish()
    }
}