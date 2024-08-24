package com.example.aprajitafoundation.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.databinding.ActivityLoginBinding
import com.example.aprajitafoundation.hideProgressDialog
import com.example.aprajitafoundation.model.UserData
import com.example.aprajitafoundation.showDialogProgress
import com.example.aprajitafoundation.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

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

        // Check if user is already signed in
        if (mAuth.currentUser != null) {
            val intent=  Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.defaultWebClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignin.setOnClickListener {
            showDialogProgress(this)
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1001)
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
                showToast(this@LoginActivity, "Error : ${exception.message}")
                hideProgressDialog()
                return
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.user != null) {

                        val userData = UserData(
                            account?.displayName,
                            account?.email,
                            account?.photoUrl.toString()
                        )
                        val gson = Gson()
                        val userDataJson = gson.toJson(userData)
                        saveInputToPreferences("google_user_data", userDataJson)

                        onAuthSuccess(task)
                    } else {
                        onError(task.exception?.message ?: "some error occurred")
                    }
                }
        }

    }
    private fun onAuthSuccess(task: Task<AuthResult>) {
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

    private fun saveInputToPreferences(key: String, value: String) {
        //Shared preference
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }




}