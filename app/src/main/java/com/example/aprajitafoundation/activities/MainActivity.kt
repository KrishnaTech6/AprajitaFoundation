package com.example.aprajitafoundation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityMainBinding
import com.example.aprajitafoundation.fragments.HomeFragment
import com.example.aprajitafoundation.fragments.EventsFragment
import com.example.aprajitafoundation.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment(), "Home")
        binding.bottomNavigationBar.selectedItemId = R.id.home

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> { replaceFragment(HomeFragment(), "Home")
                    true}
                R.id.events -> {    replaceFragment(EventsFragment(), "Events")
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
            is ProfileFragment -> binding.bottomNavigationBar.selectedItemId = R.id.profile
            else -> {
                Log.d("MainActivity","null fragment" )
            }
        }
    }

}