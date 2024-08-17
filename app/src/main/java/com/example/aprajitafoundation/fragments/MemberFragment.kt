package com.example.aprajitafoundation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.databinding.FragmentMemberBinding
import com.example.aprajitafoundation.model.MemberItem


class MemberFragment : Fragment() {
    private lateinit var binding: FragmentMemberBinding
    private var memberItem: MemberItem? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMemberBinding.inflate(layoutInflater)

        //memberItem = arguments?.getParcelable(ARG_NAME_ITEM)

        if (memberItem ==null){
            Log.d("MemberFragment", "null")
        }else{
            Log.d("MemberFragment", "$memberItem")

            binding.profileName.text = memberItem!!.name
            binding.profileDesignation.text = memberItem!!.designation

            // Using Glide for image loading
            Glide.with(requireActivity())
                .load(memberItem!!.nameImageResourceId)
                .into(binding.profilePhoto)

        }



        return binding.root
    }

    companion object{
        private const val ARG_NAME_ITEM = "arg_name_item"

//        fun newInstance(memberItem: MemberItem): MemberFragment {
//            val fragment = MemberFragment()
//            val args = Bundle().apply {
//                putParcelable(ARG_NAME_ITEM, memberItem)
//            }
//            fragment.arguments = args
//            return fragment
//        }
    }
}