package com.example.aprajitafoundation.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.databinding.FragmentEventsBinding

class EventsFragment : BaseFragment() {

    private lateinit var binding:FragmentEventsBinding
    private lateinit var viewModel: DataViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentEventsBinding.inflate(layoutInflater)

        val eventPosition: Int? = arguments?.getInt("position")

        Log.d("TAG", "onCreateView: $eventPosition")
        // Initialize the ViewModel
        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())

        val imageEventAdapter= ImageEventAdapter(requireContext(), viewModel = viewModel)
        binding.rvEvents.adapter = imageEventAdapter

        // Observe the images LiveData
        viewModel.events.observe(viewLifecycleOwner){ events ->
            imageEventAdapter.updateEvents(events)
            // SCROLL TO POSITION NOT WORKING
            Handler().postDelayed({
                eventPosition?.let { binding.rvEvents.smoothScrollToPosition(it)
                } }, 300)

        }

        return binding.root
    }


}