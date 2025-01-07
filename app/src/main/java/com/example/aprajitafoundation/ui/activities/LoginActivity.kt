package com.example.aprajitafoundation.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.databinding.ActivityLoginBinding
import com.example.aprajitafoundation.model.UserData
import com.example.aprajitafoundation.admin.LoginAdminActivity
import com.example.aprajitafoundation.utility.saveInputToPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        mAuth = FirebaseAuth.getInstance()

        binding.skipButton.setOnClickListener {
            val intent=  Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.defaultWebClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignin.setOnClickListener {
            showDialogProgress()
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1001)
        }

        binding.loginAdmin.setOnClickListener{
            val intent=  Intent(this@LoginActivity, LoginAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && data != null) {

            var account: GoogleSignInAccount? = null

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                account = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                showToast("Error : ${exception.message}")
                hideProgressDialog()
                return
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.user != null) {
                        val userData = UserData(
                            account?.id,
                            account?.email,
                            account?.displayName,
                            account?.photoUrl.toString()
                        )
                        val gson = Gson()
                        val userDataJson = gson.toJson(userData)
                        saveInputToPreferences(this, getString(R.string.google_user_data), userDataJson)

                        onAuthSuccess()
                    } else {
                        onError(task.exception?.message ?: "some error occurred")
                    }
                }
        }

    }
    private fun onAuthSuccess() {
        CoroutineScope(Dispatchers.Main).launch {
            if (this@LoginActivity.isDestroyed) return@launch

            val intent=  Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun onError(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT)
            .show()
        hideProgressDialog()
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            // This is the last activity, show the dialog
            showExitAppDialog()
        } else {
            // This is not the last activity, perform normal back button behavior
            super.onBackPressed()
        }
    }

    private fun showExitAppDialog() {
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