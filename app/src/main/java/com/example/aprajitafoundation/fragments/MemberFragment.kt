package com.example.aprajitafoundation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.databinding.FragmentMemberBinding
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.model.Socials


class MemberFragment : BaseFragment() {
    private lateinit var binding: FragmentMemberBinding
    private var memberItem: MemberModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMemberBinding.inflate(layoutInflater)

        arguments?.getParcelable<MemberModel>(ARG_NAME_ITEM)?.let { member ->
            memberItem = member
            binding.profileName.text = member.name
            binding.profileDesignation.text = member.position

            // Using Glide for image loading
            Glide.with(requireActivity())
                .load(member.image)
                .into(binding.profilePhoto)

            binding.profileAbout.text = member.description
            binding.profilePunchline.text = member.quote

            // Manage social media visibility and click listeners
            setupSocialMedia(member.socials)

        }



        return binding.root
    }

    private fun setupSocialMedia(socials: Socials?) {
        binding.ivFacebook.apply {
            visibility = if (!socials?.facebook.isNullOrEmpty()) View.VISIBLE else View.GONE
            setOnClickListener { socials?.facebook?.takeIf { it.isNotEmpty() }?.let { openWebsite(it) } }
        }

        binding.ivTwitter.apply {
            visibility = if (!socials?.twitter.isNullOrEmpty()) View.VISIBLE else View.GONE
            setOnClickListener { socials?.twitter?.takeIf { it.isNotEmpty() }?.let { openWebsite(it) } }
        }

        binding.ivInstagram.apply {
            visibility = if (!socials?.instagram.isNullOrEmpty()) View.VISIBLE else View.GONE
            setOnClickListener { socials?.instagram?.takeIf { it.isNotEmpty() }?.let { openWebsite(it) } }
        }

        binding.ivLinkedIn.apply {
            visibility = if (!socials?.linkedin.isNullOrEmpty()) View.VISIBLE else View.GONE
            setOnClickListener { socials?.linkedin?.takeIf { it.isNotEmpty() }?.let { openWebsite(it) } }
        }
    }


    companion object{
        private const val ARG_NAME_ITEM = "arg_name_item"

        fun newInstance(memberItem: MemberModel): MemberFragment {
            val fragment = MemberFragment()
            val args = Bundle().apply {
                putParcelable(ARG_NAME_ITEM, memberItem)
            }
            fragment.arguments = args
            return fragment
        }
    }
}