package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEditEventBinding
import com.example.aprajitafoundation.model.EventModel

class EditEventFragment : Fragment() {

    private lateinit var binding:FragmentEditEventBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditEventBinding.inflate(layoutInflater)

        // Retrieve the passed event data
        val event = arguments?.getParcelable<EventModel>("event")

        Log.d("EditEventFargment", event.toString())


        return binding.root
    }
}