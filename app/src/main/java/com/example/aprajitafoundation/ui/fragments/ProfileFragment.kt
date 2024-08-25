package com.example.aprajitafoundation.ui.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.DataViewModel
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.ui.activities.PaymentActivity
import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.databinding.FragmentProfileBinding
import com.example.aprajitafoundation.model.UserData
import com.example.aprajitafoundation.saveInputToPreferences
import com.example.aprajitafoundation.showSnackBar
import com.example.aprajitafoundation.showToast
import com.example.aprajitafoundation.ui.activities.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: DataViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()


        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        // Retrieve JSON string from SharedPreferences
        val userDataJson = sharedPreferences.getString("google_user_data", null)

        // Convert JSON string back to UserData object
        val gson = Gson()
        val userData: UserData? = gson.fromJson(userDataJson, UserData::class.java)

        // Check if the object is not null
        if (userData != null) {

            binding.tvNameProfile.text = userData.name
            binding.tvEmailProfile.text = userData.email
            Glide.with(requireContext())
                .load(userData.picture)
                .into(binding.ivImageProfile)

        }

        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.llWtspContact.setOnClickListener {
            openWhatsApp(Constants.phnNumber)
        }

        binding.llWebsite.setOnClickListener {
            openWebsite(Constants.website)
        }

        binding.llShare.setOnClickListener {
            shareLink(Constants.apkLink)
        }

        binding.llEmail.setOnClickListener {
            openEmail(Constants.email)
        }

        binding.llDonate.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }
        binding.ivSettings.setOnClickListener {
            Log.d("ProfileFragment", "Settings clicked")

            popUpSettingsMenu(it)
        }
        return binding.root
    }
    private fun popUpSettingsMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        val themeItem = popupMenu.menu.findItem(R.id.app_theme)

        val theme = sharedPreferences.getString("appTheme", "Light Mode")
        themeItem.title = if (theme == "Light Mode") "Dark Mode" else "Light Mode"

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sign_out -> {
                    mAuth.signOut()

                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Constants.defaultWebClientId)
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

                    googleSignInClient.signOut().addOnSuccessListener {
                        showToast(requireContext(), "Signed Out Successfully!")

                        // Clear the google_user_data from SharedPreferences
                        sharedPreferences.edit().remove("google_user_data").apply()

                        // Reset UI elements
                        binding.tvNameProfile.text = "Your Name"
                        binding.tvEmailProfile.text = "example@gmail.com"
                        binding.ivImageProfile.setImageResource(R.drawable.logo_12)

                    }.addOnFailureListener {
                        showSnackBar(requireView(), "error: ${it.message}")
                    }
                    true
                }

                R.id.app_theme -> {
                    val currentMode = AppCompatDelegate.getDefaultNightMode()

                    if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                        // Switch to light mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        saveInputToPreferences(requireContext(), "appTheme", "Light Mode")
                        themeItem.title = "Dark Mode"
                    } else {
                        // Switch to dark mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        saveInputToPreferences(requireContext(), "appTheme", "Dark Mode")
                        themeItem.title = "Light Mode"
                    }

                    activity?.recreate()
                    true
                }

                else -> false
            }

        }
        popupMenu.show()
    }


}
