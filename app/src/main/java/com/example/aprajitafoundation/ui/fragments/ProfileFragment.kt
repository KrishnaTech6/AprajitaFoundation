package com.example.aprajitafoundation.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.ui.activities.PaymentActivity
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.databinding.FragmentProfileBinding
import com.example.aprajitafoundation.model.UserData
import com.example.aprajitafoundation.utility.saveInputToPreferences
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.ui.activities.LoginActivity
import com.example.aprajitafoundation.utility.AnimationUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.itextpdf.kernel.pdf.PdfName.r


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

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.apppreferences), MODE_PRIVATE)
        // Retrieve JSON string from SharedPreferences
        val userDataJson = sharedPreferences.getString(getString(R.string.google_user_data), null)

        // Convert JSON string back to UserData object
        val gson = Gson()
        val userData: UserData? = gson.fromJson(userDataJson, UserData::class.java)

        AnimationUtils.fadeIn(binding.tvNameProfile, 700)
        // Slide in the profile image from the bottom
        AnimationUtils.slideInFromBottom(binding.ivImageProfile, 700)
        // Check if the object is not null
        if (userData != null) {
            binding.btnLogin.visibility = View.INVISIBLE
            binding.tvNameProfile.text = userData.name
            binding.tvEmailProfile.text = userData.email
            Glide.with(requireContext())
                .load(userData.picture)
                .into(binding.ivImageProfile)

        }else{
            binding.btnLogin.visibility = View.VISIBLE
        }

        binding.btnLogin.setOnClickListener{
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
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

        binding.llAboutus.setOnClickListener{
            showCustomDialog()
        }
        return binding.root
    }

    private fun showCustomDialog() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.about_us, null)

        // Create the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val dialogButton: Button = dialogView.findViewById(R.id.btn_ok)
        dialogButton.setOnClickListener{
            dialog.cancel()
        }

        dialog.show()

    }

    private fun popUpSettingsMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        val themeItem = popupMenu.menu.findItem(R.id.app_theme)

        val theme = sharedPreferences.getString(getString(R.string.apptheme), getString(R.string.light_mode))
        themeItem.title = if (theme == getString(R.string.light_mode)) getString(R.string.dark_mode) else getString(R.string.light_mode)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sign_out -> {

                    if(mAuth.currentUser != null){
                        mAuth.signOut()

                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(Constants.defaultWebClientId)
                            .requestEmail()
                            .build()

                        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

                        googleSignInClient.signOut().addOnSuccessListener {
                            showToast(requireContext(), "Signed Out Successfully!")

                            // Clear the google_user_data from SharedPreferences
                            with(sharedPreferences.edit()) {
                                //remove payment details on signout
                                remove(getString(R.string.name_payment))
                                remove(getString(R.string.email_payment))
                                remove(getString(R.string.phone_payment))
                                //remove google user data
                                remove(getString(R.string.google_user_data))
                            }.apply()

                            // Reset UI elements
                            binding.btnLogin.visibility = View.VISIBLE
                            binding.tvNameProfile.text = "Your Name"
                            binding.tvEmailProfile.text = "example@gmail.com"
                            binding.ivImageProfile.setImageResource(R.drawable.logo_12)

                            //To go to signin screen
                            val intent = Intent(activity, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }.addOnFailureListener {
                            showSnackBar(requireView(), "error: ${it.message}")
                        }
                    }else{
                        showToast(requireContext(), "No User")
                    }

                    true
                }

                R.id.app_theme -> {
                    val currentMode = AppCompatDelegate.getDefaultNightMode()

                    if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                        // Switch to light mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        saveInputToPreferences(requireContext(), getString(R.string.apptheme), getString(R.string.light_mode))
                        themeItem.title = getString(R.string.dark_mode)
                    } else {
                        // Switch to dark mode
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        saveInputToPreferences(requireContext(), getString(R.string.apptheme), getString(R.string.dark_mode))
                        themeItem.title = getString(R.string.light_mode)
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
