package com.example.aprajitafoundation.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.aprajitafoundation.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //to hide actionbar
        supportActionBar?.hide()

        //to hide statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        mAuth = FirebaseAuth.getInstance()


        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val theme = sharedPreferences.getString("appTheme", "Light Mode")

// Check if the current mode does not match the stored theme
        if (theme == "Dark Mode" && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if (theme == "Light Mode" && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }



        //to show splash screen for 15s and go to main screen
        Handler(Looper.getMainLooper()).postDelayed(
            {
                // Check if user is already signed in
                if (mAuth.currentUser != null) {
                    val intent=  Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000
        )
    }
}