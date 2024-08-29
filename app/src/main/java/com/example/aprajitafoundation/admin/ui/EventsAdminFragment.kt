package com.example.aprajitafoundation.admin.ui

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEvents2Binding

class EventsAdminFragment : Fragment() {
    private lateinit var binding: FragmentEvents2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEvents2Binding.inflate(layoutInflater)

        return binding.root
    }
}