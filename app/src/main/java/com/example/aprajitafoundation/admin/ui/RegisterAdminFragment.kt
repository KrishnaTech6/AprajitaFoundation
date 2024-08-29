package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentRegisterAdminBinding


class RegisterAdminFragment : Fragment() {
    private lateinit var binding: FragmentRegisterAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterAdminBinding.inflate(layoutInflater)





        return binding.root
    }

}