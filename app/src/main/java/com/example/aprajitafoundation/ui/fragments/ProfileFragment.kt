package com.example.aprajitafoundation.ui.fragments

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
import com.example.aprajitafoundation.showToast
import com.google.gson.Gson


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: DataViewModel
    private lateinit var sharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

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
            val intent=  Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }
        binding.ivSettings.setOnClickListener{
            Log.d("ProfileFragment", "Settings clicked")
            popUpSettingsMenu(it)
        }

        // Observe the app theme in onViewCreated
        viewModel.appTheme.observe(viewLifecycleOwner) { theme ->
            // Update the theme based on the current value
            if (theme == "Light Mode") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else if (theme == "Dark Mode") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }



        return binding.root
    }

    private fun popUpSettingsMenu(view: View){
        val popupMenu= PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        val themeItem = popupMenu.menu.findItem(R.id.app_theme)


        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.sign_out ->{
                    showToast(requireContext(), "Sign out clicked")
                    true}
                R.id.app_theme ->{
                    themeItem.title = viewModel.appTheme.value
                    // Set the menu item title based on the current theme
                    themeItem.title = if (viewModel.appTheme.value == "Light Mode") "Dark Mode" else "Light Mode"

                    // Switch the theme when the user clicks the theme menu item
                    val newTheme = if (themeItem.title == "Dark Mode") "Light Mode" else "Dark Mode"
                    viewModel.setAppTheme(newTheme) // Update the theme in the ViewModel
                    themeItem.title = newTheme // Update the menu item title
                    true

                    true}
                else ->false
            }

        }
        popupMenu.show()
    }


}
