package com.example.aprajitafoundation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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