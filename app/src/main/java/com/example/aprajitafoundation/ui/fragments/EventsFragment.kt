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
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())

        // Observe the images LiveData
        viewModel.events.observe(viewLifecycleOwner){ events ->
            val imageEventAdapter= ImageEventAdapter(requireContext(), eventItems =  events, viewModel = viewModel)
            binding.rvEvents.adapter = imageEventAdapter
// SCROLL TO POSITION NOT WORKING
            Handler().postDelayed({ eventPosition?.let { binding.rvEvents.scrollToPosition(it) } }, 2500)
        }
        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) handleLoadingState(requireView())
            else hideProgressDialog()
        }
        viewModel.error.observe(viewLifecycleOwner){
            showToast( it)
        }

        // Fetch the all events
        viewModel.fetchAllEvents()

        return binding.root
    }


}