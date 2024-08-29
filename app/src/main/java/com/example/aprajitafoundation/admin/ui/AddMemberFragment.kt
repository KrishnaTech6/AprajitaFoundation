package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentAddMemberBinding
import com.example.aprajitafoundation.ui.adapter.ImageAdapter
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.ui.fragments.MemberFragment
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.google.android.play.integrity.internal.l

class AddMemberFragment : Fragment() {
    private lateinit var binding: FragmentAddMemberBinding
    private lateinit var viewModel: DataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddMemberBinding.inflate(layoutInflater)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        /*THIS IS THE CODE FOR NAME, IMAGE, DESIGNATION  RECYCLERVIEW */
        val memberAdapter =
            ImageAdapter(requireContext(), listOf(), viewModel = viewModel, isAdmin = true) {
                //action onclick
            }
// Set the adapter and layout manager
        binding.rvAddMembers.adapter = memberAdapter
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvAddMembers.layoutManager = staggeredGridLayoutManager
        binding.rvAddMembers.setHasFixedSize(true)

// Observe the members LiveData from the ViewModel
        viewModel.members.observe(viewLifecycleOwner) { members ->
            // Update the adapter with the new list
            Log.d("Members Data", "Members: $members")
            memberAdapter.updateMembers(members)
        }
        viewModel.deleteResponse.observe(viewLifecycleOwner) {
            Log.d("ViewModel", it.message)
            showToast(requireContext(), it.message)

            //get updated list from server after deletion
            viewModel.fetchTeamMembers()

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
        viewModel.error.observe(viewLifecycleOwner) {
            showToast(requireContext(), it)
        }

        // Fetch the all events
        viewModel.fetchTeamMembers()




        return binding.root
    }
}