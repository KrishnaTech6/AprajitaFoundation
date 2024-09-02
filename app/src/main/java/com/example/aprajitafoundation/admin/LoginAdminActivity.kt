package com.example.aprajitafoundation.admin

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.api.LoginRequest
import com.example.aprajitafoundation.databinding.ActivityLoginAdminBinding
import com.example.aprajitafoundation.ui.activities.LoginActivity
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.AdminAuthViewModel

class LoginAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAdminBinding
    private lateinit var viewModel: AdminAuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
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

        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]


        binding.loginButton.setOnClickListener {
            if(isDetailsValid()) {
                val request = LoginRequest(
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )
                viewModel.loginAdmin(request, this@LoginAdminActivity)
            }
        }

        viewModel.authResponse.observe(this) { response ->
            showToast(this, response.message)

            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.error.observe(this) { error ->
            showToast(this, error)
        }

        viewModel.loading.observe(this) { isLoading ->
            if(isLoading) showDialogProgress(this)
            else hideProgressDialog()
        }


    }

    private fun isDetailsValid(): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return when{
            binding.emailEditText.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Email field is empty!")
                false
            }
            binding.emailEditText.text.isNullOrBlank() -> {
                showSnackBar(binding.root, "Email field is empty!")
                false
            }
            !binding.emailEditText.text.toString().matches(emailPattern)->{
                showSnackBar(binding.root, "Invalid email!")
                false
            }
            else -> true
        }

    }
    override fun onBackPressed() {
        if (isTaskRoot) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            // This is not the last activity, perform normal back button behavior
            super.onBackPressed()
        }
    }

}