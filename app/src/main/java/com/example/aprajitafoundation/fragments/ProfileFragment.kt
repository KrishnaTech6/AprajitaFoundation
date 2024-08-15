package com.example.aprajitafoundation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        return binding.root
    }
}