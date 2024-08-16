package com.example.aprajitafoundation.fragments

import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.databinding.FragmentMemberBinding
import com.example.aprajitafoundation.model.NameItem


class MemberFragment : Fragment() {
    private lateinit var binding: FragmentMemberBinding
    private var nameItem: NameItem? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMemberBinding.inflate(layoutInflater)

        nameItem = arguments?.getParcelable(ARG_NAME_ITEM)

        if (nameItem ==null){
            Log.d("MemberFragment", "null")
        }else{
            Log.d("MemberFragment", "$nameItem")

            binding.profileName.text = nameItem!!.Name
            binding.profileDesignation.text = nameItem!!.designation

            // Using Glide for image loading
            Glide.with(requireActivity())
                .load(nameItem!!.nameImageResourceId)
                .into(binding.profilePhoto)

        }



        return binding.root
    }

    companion object{
        private const val ARG_NAME_ITEM = "arg_name_item"

        fun newInstance(nameItem: NameItem): MemberFragment {
            val fragment = MemberFragment()
            val args = Bundle().apply {
                putParcelable(ARG_NAME_ITEM, nameItem)
            }
            fragment.arguments = args
            return fragment
        }
    }
}