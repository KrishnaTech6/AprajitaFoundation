package com.example.aprajitafoundation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.databinding.FragmentGalleryBinding

class GalleryFragment : BaseFragment() {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var viewModel: DataViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentGalleryBinding.inflate(layoutInflater)
        // Initialize the ViewModel
        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]

        binding.rvGallery.adapter = ImageEventAdapter(requireContext(), listOf(), viewModel = viewModel)
        binding.rvGallery.setHasFixedSize(true)

        // Observe the images LiveData
        viewModel.allImages.observe(viewLifecycleOwner){ images ->
            val imageEventAdapter= ImageEventAdapter(requireContext(), imageItems =  images, viewModel = viewModel)
            binding.rvGallery.adapter = imageEventAdapter
            imageEventAdapter.notifyDataSetChanged()
        }
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvGallery.layoutManager = staggeredGridLayoutManager

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) handleLoadingState( requireView())
            else hideProgressDialog()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            showToast(it)
        }

        return binding.root
    }
}