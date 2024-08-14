package com.example.aprajitafoundation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.aprajitafoundation.adapter.ImageAdapter
import com.example.aprajitafoundation.adapter.ImageEventAdapter
import com.example.aprajitafoundation.adapter.SliderAdapter
import com.example.aprajitafoundation.data.DataSource
import com.example.aprajitafoundation.databinding.FragmentEventsBinding

class EventsFragment : Fragment() {

    private lateinit var binding:FragmentEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentEventsBinding.inflate(layoutInflater)


        val imageItem = DataSource().loadImageData()
        binding.rvEvents.adapter = ImageEventAdapter(requireContext(), imageItem)
        binding.rvEvents.setHasFixedSize(true)

        return binding.root
    }


}