package com.example.aprajitafoundation.admin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.ActivityLoginAdminBinding
import com.example.aprajitafoundation.model.LoginRequest
import com.example.aprajitafoundation.ui.activities.BaseActivity
import com.example.aprajitafoundation.ui.activities.LoginActivity
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.viewmodel.AdminAuthViewModel

class LoginAdminActivity : BaseActivity() {
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

        viewModel.authResponseLogin.observe(this) { response ->
            showToast(response.message)
            hideProgressDialog()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }, 100)
        }

        viewModel.error.observe(this) { error ->
            showToast( error)
        }

        // Observe the loading LiveData
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) handleLoadingState( binding.root)
            else hideProgressDialog()
        }
    }

    private fun isDetailsValid(): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return when{
            binding.emailEditText.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_email_empty))
                false
            }
            binding.passwordEditText.text.isNullOrBlank() -> {
                showSnackBar(binding.root, getString(R.string.error_password_empty))
                false
            }
            !binding.emailEditText.text.toString().matches(emailPattern)->{
                showSnackBar(binding.root, getString(R.string.error_invalid_email_address))
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