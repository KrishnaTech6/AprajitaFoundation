package com.example.aprajitafoundation.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityAdminBinding
import com.example.aprajitafoundation.ui.activities.LoginActivity
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.AdminAuthViewModel

class AdminActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAdminBinding

    private lateinit var viewModel: AdminAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarAdmin.toolbar)

        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]


        viewModel.error.observe(this){
            showToast(this, it)
        }

        viewModel.loading.observe(this){
            if(it) showDialogProgress(this) else hideProgressDialog()
        }


        binding.appBarAdmin.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home_admin,
                R.id.nav_gallery_admin,
                R.id.nav_events_admin,
                R.id.nav_register_admin,
                R.id.nav_payments,
                R.id.nav_profile_admin,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_signout ->{
                viewModel.logout(this@AdminActivity)

                viewModel.genericResponse.observe(this@AdminActivity){
                    showToast(this@AdminActivity, it.message)

                    //clear tasks and go to LoginActivity
                    val intent = Intent (this , LoginAdminActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                true
            }
            else->super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            // This is the last activity, show the dialog
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
        } else {
            // This is not the last activity, perform normal back button behavior
            super.onBackPressed()
        }
    }

}