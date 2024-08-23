package com.example.aprajitafoundation.ui.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityMainBinding
import com.example.aprajitafoundation.ui.fragments.HomeFragment
import com.example.aprajitafoundation.ui.fragments.EventsFragment
import com.example.aprajitafoundation.ui.fragments.GalleryFragment
import com.example.aprajitafoundation.ui.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        replaceFragment(HomeFragment(), "Home")
        binding.bottomNavigationBar.selectedItemId = R.id.home

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> { replaceFragment(HomeFragment(), "Home")
                    true}
                R.id.events -> {    replaceFragment(EventsFragment(), "Events")
                    true}
                R.id.gallery -> {   replaceFragment(GalleryFragment(), "Gallery")
                    true}
                R.id.profile -> {   replaceFragment(ProfileFragment(), "Profile")
                    true}
                else -> false

            }
        }

        // Add a back stack listener to handle bottom navigation highlighting
        supportFragmentManager.addOnBackStackChangedListener {
            updateBottomNavHighlight()
        }
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            // Create an AlertDialog to ask the user if they want to exit
            AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Do you really want to exit the app?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun replaceFragment(fragment: Fragment, tag:String){
        val fragmentManager = supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(tag)

        if (existingFragment !=null){
            fragmentManager.popBackStack(tag, 0)
        }else{
            fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, tag)
                .addToBackStack(tag)
                .commit()
        }
    }

    private fun updateBottomNavHighlight() {
        when (supportFragmentManager.findFragmentById(R.id.frame_layout)) {
            is HomeFragment -> binding.bottomNavigationBar.selectedItemId = R.id.home
            is EventsFragment -> binding.bottomNavigationBar.selectedItemId = R.id.events
            is GalleryFragment -> binding.bottomNavigationBar.selectedItemId = R.id.gallery
            is ProfileFragment -> binding.bottomNavigationBar.selectedItemId = R.id.profile
            else -> {
                Log.d("MainActivity","null fragment" )
            }
        }
    }

}