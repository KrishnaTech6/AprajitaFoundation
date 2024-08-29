package com.example.aprajitafoundation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.databinding.FragmentEventsBinding
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast

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
        binding.rvEvents.adapter = ImageEventAdapter(requireContext(), listOf(), viewModel = viewModel)
        binding.rvEvents.setHasFixedSize(true)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Observe the images LiveData
        viewModel.events.observe(viewLifecycleOwner){ events ->
            val imageEventAdapter= ImageEventAdapter(requireContext(), eventItems =  events, viewModel = viewModel)
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
        viewModel.error.observe(viewLifecycleOwner){
            showToast(requireContext(), it)
        }

        // Fetch the all events
        viewModel.fetchAllEvents()

        return binding.root
    }


}