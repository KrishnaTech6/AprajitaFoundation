package com.example.aprajitafoundation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aprajitafoundation.databinding.AboutUsBinding


class AboutUsFragment: BaseFragment() {

    private lateinit var binding: AboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AboutUsBinding.inflate(layoutInflater)



        return binding.root
    }

}