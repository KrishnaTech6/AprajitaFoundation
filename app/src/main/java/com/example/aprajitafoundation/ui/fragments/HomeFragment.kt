package com.example.aprajitafoundation.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.aprajitafoundation.viewmodel.DataViewModel
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.ui.activities.PaymentActivity
import com.example.aprajitafoundation.ui.adapter.ImageAdapter
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.databinding.FragmentHomeBinding
import com.example.aprajitafoundation.utility.hideProgressDialog
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.utility.showDialogProgress
import com.example.aprajitafoundation.utility.showSnackBar
import com.example.aprajitafoundation.utility.showToast

class HomeFragment : BaseFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var binding: FragmentHomeBinding

    private val handler = Handler(Looper.getMainLooper())

    private var sliderDataList = listOf<ImageModel>()
    private val autoSlideDelay: Long = 3000 // 3 seconds

    private lateinit var viewModel: DataViewModel

    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            if(sliderDataList.isNotEmpty()){
                val nextItem =
                    if (viewPager.currentItem == sliderDataList.size - 1) 0 else viewPager.currentItem + 1
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, autoSlideDelay) // Schedule next slide
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        // Fetch the gallery images and members
        viewModel.fetchGalleryImages()
        viewModel.fetchTeamMembers()

        // Observe the images LiveData
        viewModel.images.observe(viewLifecycleOwner) { images ->
            if(images!=null){
                val imageEventAdapter = ImageEventAdapter(requireContext(), images, viewModel = viewModel)
                binding.rvImageItem.adapter = imageEventAdapter
                val staggeredGridLayoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.rvImageItem.layoutManager = staggeredGridLayoutManager
                binding.rvImageItem.setHasFixedSize(true)

                //For runnable
                sliderDataList = images
                /*THIS IS THE CODE FOR AUTOMATIC SLIDER */
                viewPager = binding.viewPager
                viewPager.adapter = ImageEventAdapter(requireContext(), images, isSlider = true, viewModel = viewModel)

                imageEventAdapter.notifyDataSetChanged()
                createDots(binding.dotsLayout, sliderDataList.size) //to show dots below slider
            }
        }


        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state (e.g., show/hide a ProgressBar)
            if (isLoading) {
                showDialogProgress(requireContext())
                if (!isInternetAvailable(requireContext())) {
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

        /*THIS IS THE CODE FOR NAME, IMAGE, DESIGNATION  RECYCLERVIEW */

        val memberAdapter = ImageAdapter(requireContext(), listOf(), viewModel = viewModel) { member ->
            //OnClick Action
            val memberFragment = MemberFragment.newInstance(member)
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, memberFragment) // Use your container ID
                .addToBackStack(null)
                .commit()
        }

        // Set the adapter and layout manager
        binding.rvItems.adapter = memberAdapter
        binding.rvItems.setHasFixedSize(true)

        // Observe the members LiveData from the ViewModel
        viewModel.members.observe(viewLifecycleOwner) { members ->
            if(members!=null){
                // Update the adapter with the new list
                Log.d("Members Data", "Members: $members")
                memberAdapter.updateMembers(members)
            }
        }
        //for donations:razorpay
        binding.btnDonate.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }

        //Go to gallery fragment
        binding.llGoToGallery.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, GalleryFragment(), getString(R.string.gallery_fragment_tag))
                .addToBackStack(getString(R.string.gallery_fragment_tag))
                .commit()

        }

        return binding.root

    }
    private fun createDots(dotsLayout: LinearLayout, size: Int) {
        dotsLayout.removeAllViews()
        val dots = arrayOfNulls<ImageView>(size)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4, 2, 4, 2)
        }

        for (i in 0 until size) {
            dots[i] = ImageView(requireContext()).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.dot_inactive
                    )
                )
                layoutParams = params
            }
            dotsLayout.addView(dots[i])
        }

        dots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dot_active
            )
        )

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { index, imageView ->
                    imageView?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            if (index == position) R.drawable.dot_active else R.drawable.dot_inactive
                        )
                    )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoSlideRunnable, autoSlideDelay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoSlideRunnable)

    }

}