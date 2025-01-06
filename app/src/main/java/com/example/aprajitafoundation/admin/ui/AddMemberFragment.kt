package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentAddMemberBinding
import com.example.aprajitafoundation.ui.adapter.ImageAdapter
import com.example.aprajitafoundation.utility.handleLoadingState
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast
import com.example.aprajitafoundation.viewmodel.DataViewModel

class AddMemberFragment : Fragment() {
    private lateinit var binding: FragmentAddMemberBinding
    private lateinit var viewModel: DataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
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
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvAddMembers.layoutManager = staggeredGridLayoutManager
        binding.rvAddMembers.setHasFixedSize(true)

        // Observe the members LiveData from the ViewModel
        viewModel.members.observe(viewLifecycleOwner) { members ->
            // Update the adapter with the new list
            Log.d("Members Data", "Members: $members")
            memberAdapter.updateMembers(members)
        }
        viewModel.deleteResponse.observe(viewLifecycleOwner) {
            showToast(requireContext(), it.message)

            //do this before calling fetch again
            hideProgressDialog()
            //fetch updated team members from server
            viewModel.fetchTeamMembers()

        }

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) handleLoadingState(requireContext(), requireView())
            else hideProgressDialog()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            showToast(requireContext(), it)
        }


        binding.btnAddMember.setOnClickListener{
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_admin)
            navController.navigate(R.id.action_nav_team_member_to_editMemberFragment)

        }

        // Fetch the all events
        viewModel.fetchTeamMembers()

        return binding.root
    }
}