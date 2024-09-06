package com.example.aprajitafoundation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.databinding.FragmentGalleryBinding
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast

class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var viewModel: DataViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentGalleryBinding.inflate(layoutInflater)
        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

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
            // Handle loading state (e.g., show/hide a ProgressBar)
            if (isLoading) {
                showDialogProgress(requireContext())
                if(!isInternetAvailable(requireContext())){
                    hideProgressDialog()
                    showSnackBar(requireView(), getString(R.string.no_internet_connection))
                }
            } else {
                hideProgressDialog()
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            showToast(requireContext(), it)
        }

        // Fetch the all images
        viewModel.fetchAllGalleryImages()

        return binding.root
    }
}