package com.example.aprajitafoundation.admin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aprajitafoundation.databinding.FragmentEditMemberBinding
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.MemberModel


class EditMemberFragment : Fragment() {
    private lateinit var binding: FragmentEditMemberBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditMemberBinding.inflate(layoutInflater)

        // Retrieve the passed event data
        val member = arguments?.getParcelable<MemberModel>("member")
        Log.d("EditMemberFragment", member.toString())


        return binding.root
    }
}