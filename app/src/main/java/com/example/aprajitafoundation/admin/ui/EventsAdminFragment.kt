package com.example.aprajitafoundation.admin.ui

import android.media.tv.TableRequest
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentEvents2Binding
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel

class EventsAdminFragment : Fragment() {
    private lateinit var binding: FragmentEvents2Binding
    private lateinit var viewModel: DataViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentEvents2Binding.inflate(layoutInflater)
        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        //if item deleted then result is obtained here
        viewModel.deleteResponse.observe(viewLifecycleOwner) {
            showToast(requireContext(), it.message)
            //do this before calling fetch again
            hideProgressDialog()
            //Again fetch events
            viewModel.fetchAllEvents()

        }

        // Observe the images LiveData
        viewModel.events.observe(viewLifecycleOwner) { events ->
            val imageEventAdapter = ImageEventAdapter(
                requireContext(),
                eventItems = events,
                viewModel = viewModel,
                isAdmin = true
            )
            binding.rvEvents.adapter = imageEventAdapter
            imageEventAdapter.notifyDataSetChanged()
        }

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state (e.g., show/hide a ProgressBar)
            if (isLoading) {
                showDialogProgress(requireContext())
                if (!isInternetAvailable(requireContext())) {
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

        binding.btnAddEvent.setOnClickListener{
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigate(R.id.action_nav_events_admin_to_editEventFragment)
        }

        // Fetch the all events
        viewModel.fetchAllEvents()

        return binding.root
    }
}