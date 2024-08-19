package com.example.aprajitafoundation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityMainBinding
import com.example.aprajitafoundation.fragments.DashboardFragment
import com.example.aprajitafoundation.fragments.EventsFragment
import com.example.aprajitafoundation.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(DashboardFragment())
        binding.bottomNavigationBar.selectedItemId = R.id.dashboard

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.dashboard -> { replaceFragment(DashboardFragment())
                    true}
                R.id.events -> {    replaceFragment(EventsFragment())
                    true}
                R.id.profile -> {   replaceFragment(ProfileFragment())
                    true}

                else -> false

            }
        }

        // Add a back stack listener to handle bottom navigation highlighting
//        supportFragmentManager.addOnBackStackChangedListener {
//            updateBottomNavHighlight()
//        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)

        if (currentFragment is DashboardFragment) {
            // Create an AlertDialog to ask the user if they want to exit
            AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Do you really want to exit the app?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    super.onBackPressed()
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            // If it's not DashboardFragment, perform the normal back press action
            super.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }

//    private fun updateBottomNavHighlight() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
//        when (currentFragment) {
//            is DashboardFragment -> binding.bottomNavigationBar.selectedItemId = R.id.dashboard
//            is EventsFragment -> binding.bottomNavigationBar.selectedItemId = R.id.events
//            is ProfileFragment -> binding.bottomNavigationBar.selectedItemId = R.id.profile
//        }
//    }

}