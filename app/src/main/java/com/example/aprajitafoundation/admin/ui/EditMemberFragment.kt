package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aprajitafoundation.databinding.FragmentEditMemberBinding


class EditMemberFragment : Fragment() {
    private lateinit var binding: FragmentEditMemberBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditMemberBinding.inflate(layoutInflater)




        return binding.root
    }
}