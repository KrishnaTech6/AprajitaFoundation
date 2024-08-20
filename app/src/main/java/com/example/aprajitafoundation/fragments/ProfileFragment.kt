package com.example.aprajitafoundation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.activities.MainActivity
import com.example.aprajitafoundation.activities.PaymentActivity
import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.databinding.FragmentDashboardBinding
import com.example.aprajitafoundation.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

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
            popUpSetttingsMenu(it)
        }

        return binding.root
    }

    private fun popUpSetttingsMenu(view: View){
        val popupMenu= PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        val themeItem = popupMenu.menu.findItem(R.id.app_theme)


        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.sign_out ->{
                    showToast("Sign out clicked")
                    true}
                R.id.app_theme ->{
                    if(themeItem.title == "Light Mode"){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        themeItem?.title = "Dark Mode"
                    }else if ( themeItem.title == "Dark Mode"){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        themeItem?.title = "Light Mode"
                    }

                    true}
                else ->false
            }

        }
        popupMenu.show()
    }
}