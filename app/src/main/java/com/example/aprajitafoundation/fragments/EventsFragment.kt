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
import com.example.aprajitafoundation.adapter.ImageAdapter
import com.example.aprajitafoundation.adapter.ImageEventAdapter
import com.example.aprajitafoundation.adapter.SliderAdapter
import com.example.aprajitafoundation.data.DataSource
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


        //val imageItem = DataSource().loadImageData()
        binding.rvEvents.adapter = ImageEventAdapter(requireContext(), listOf())
        binding.rvEvents.setHasFixedSize(true)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Observe the images LiveData
        viewModel.images.observe(viewLifecycleOwner){ images ->
            val imageEventAdapter= ImageEventAdapter(requireContext(), images)
            binding.rvEvents.adapter = imageEventAdapter
            imageEventAdapter.notifyDataSetChanged()
        }

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state (e.g., show/hide a ProgressBar)
            if(isLoading){
                showDialogProgress()
            }else{
                hideProgressDialog()
            }
        }

        // Fetch the gallery images
        viewModel.fetchGalleryImages()

        return binding.root
    }


}