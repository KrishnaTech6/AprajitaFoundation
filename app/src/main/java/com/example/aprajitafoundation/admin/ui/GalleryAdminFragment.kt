package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.databinding.FragmentGallery2Binding
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel

class GalleryAdminFragment : Fragment() {

    private var _binding: FragmentGallery2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel:DataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentGallery2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        binding.rvGalleryAdmin.adapter = ImageEventAdapter(requireContext(), listOf(), isAdmin = true, viewModel = viewModel)
        binding.rvGalleryAdmin.setHasFixedSize(true)



        // Observe the images LiveData
        viewModel.allImages.observe(viewLifecycleOwner){ images ->

            Log.d("ViewModel", "$images")
            val imageEventAdapter= ImageEventAdapter(requireContext(), imageItems =  images, isAdmin = true, viewModel = viewModel)
            binding.rvGalleryAdmin.adapter = imageEventAdapter
            imageEventAdapter.notifyDataSetChanged()
        }

        viewModel.deleteResponse.observe(viewLifecycleOwner){
            //response after successful deletion of image
            showToast(requireContext(), it.message)
            //now again fetch the updated list from the server
            viewModel.fetchAllGalleryImages()
        }

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvGalleryAdmin.layoutManager = staggeredGridLayoutManager

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

        // Fetch the all images
        viewModel.fetchAllGalleryImages()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}