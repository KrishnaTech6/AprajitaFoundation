package com.example.aprajitafoundation.admin

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.admin.ui.ProfileAdminFragment
import com.example.aprajitafoundation.api.User
import com.example.aprajitafoundation.databinding.ActivityAdminBinding
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.ui.activities.LoginActivity
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.initCloudinary
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.AdminAuthViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdminActivity : AppCompatActivity(), ProfileAdminFragment.OnProfileUpdatedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAdminBinding

    private lateinit var viewModel: AdminAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialised Cloudinary
        initCloudinary(this)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarAdmin.toolbar)

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

        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]


        viewModel.error.observe(this) {
            showToast(this, it)
        }

        viewModel.loading.observe(this) {
            if (it) showDialogProgress(this) else hideProgressDialog()
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
                R.id.nav_team_member,
                R.id.nav_payments,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val navHeader = navView.getHeaderView(0)
        val tvAdminName = navHeader.findViewById<TextView>(R.id.text_admin_name)
        val tvAdminProfileImage = navHeader.findViewById<ImageView>(R.id.iv_admin)
        val tvAdminEmail = navHeader.findViewById<TextView>(R.id.text_admin_email)
        val editProfile = navHeader.findViewById<ImageView>(R.id.iv_edit_profile_admin)
        val gson = Gson()
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedUserJson = sharedPreferences.getString("user", "")
        val type = object : TypeToken<User>() {}.type

        val savedUser = gson.fromJson<User>(savedUserJson, type)
        tvAdminName.text = savedUser.name
        tvAdminEmail.text = savedUser.email
        Glide.with(this)
            .load(savedUser.profileImg)
            .thumbnail(0.1f)
            .into(tvAdminProfileImage)

        tvAdminProfileImage.setOnClickListener{
                val intent = Intent(this@AdminActivity, FullScreenImageActivity::class.java)
                intent.putExtra("image_url", savedUser.profileImg)
                startActivity(intent)
        }

        editProfile.setOnClickListener{
            // Navigate to ProfileFragment
            navController.navigate(R.id.nav_profile_admin)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signout -> {
                viewModel.logout(this@AdminActivity)

                viewModel.genericResponse.observe(this@AdminActivity) {
                    showToast(this@AdminActivity, it.message)

                    //clear tasks and go to LoginActivity
                    val intent = Intent(this, LoginAdminActivity::class.java)
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

    override fun onProfileUpdated(name: String, email: String, profileImg: String?) {
        val navView: NavigationView = binding.navView
        val navHeader = navView.getHeaderView(0)
        val tvAdminName = navHeader.findViewById<TextView>(R.id.text_admin_name)
        val tvAdminProfileImage = navHeader.findViewById<ImageView>(R.id.iv_admin)
        val tvAdminEmail = navHeader.findViewById<TextView>(R.id.text_admin_email)

        tvAdminName.text = name
        tvAdminEmail.text = email
        Glide.with(this)
            .load(profileImg)
            .thumbnail(0.1f)
            .into(tvAdminProfileImage)

    }

}