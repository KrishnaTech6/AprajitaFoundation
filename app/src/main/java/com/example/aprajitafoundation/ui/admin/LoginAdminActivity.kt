package com.example.aprajitafoundation.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.api.LoginRequest
import com.example.aprajitafoundation.databinding.ActivityLoginAdminBinding
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

        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]


        binding.loginButton.setOnClickListener {
            if(isDetailsValid()) {
                val request = LoginRequest(
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )
                viewModel.loginAdmin(request)
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
}