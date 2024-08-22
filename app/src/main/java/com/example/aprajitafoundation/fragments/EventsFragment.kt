package com.example.aprajitafoundation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.aprajitafoundation.DataViewModel
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.adapter.ImageAdapter
import com.example.aprajitafoundation.adapter.ImageEventAdapter
import com.example.aprajitafoundation.adapter.SliderAdapter
import com.example.aprajitafoundation.data.DataSource
import com.example.aprajitafoundation.databinding.FragmentEventsBinding
import com.example.aprajitafoundation.hideProgressDialog
import com.example.aprajitafoundation.isInternetAvailable
import com.example.aprajitafoundation.showDialogProgress
import com.example.aprajitafoundation.showSnackBar

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


        //val imageItem = DataSource().loadImageData()
        binding.rvEvents.adapter = ImageEventAdapter(requireContext(), listOf())
        binding.rvEvents.setHasFixedSize(true)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Observe the images LiveData
        viewModel.events.observe(viewLifecycleOwner){ events ->
            val imageEventAdapter= ImageEventAdapter(requireContext(), eventItems =  events)
            binding.rvEvents.adapter = imageEventAdapter
            imageEventAdapter.notifyDataSetChanged()
        }

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state (e.g., show/hide a ProgressBar)
            if (isLoading) {
                showDialogProgress(requireContext())
                if(!isInternetAvailable(requireContext())){
                    hideProgressDialog()
                    showSnackBar(requireView(), "No Internet Connection!")
                }
            } else {
                hideProgressDialog()
            }
        }

        // Fetch the all events
        viewModel.fetchAllEvents()

        return binding.root
    }


}