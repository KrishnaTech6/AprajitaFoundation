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
import com.example.aprajitafoundation.ui.adapter.ImageAdapter
import com.example.aprajitafoundation.ui.adapter.ImageEventAdapter
import com.example.aprajitafoundation.databinding.FragmentHomeBinding
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.ui.activities.UpiPaymentActivity

class HomeFragment : BaseFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var binding: FragmentHomeBinding

    private val handler = Handler(Looper.getMainLooper())

    private var sliderDataList = listOf<EventModel>()
    private val autoSlideDelay: Long = 3000 // 3 seconds

    private lateinit var viewModel: DataViewModel

    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            if (sliderDataList.isNotEmpty()) {
                val nextItem = (viewPager.currentItem + 1) % sliderDataList.size
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, autoSlideDelay) // Schedule the next slide
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
        viewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)

        // Observe the images LiveData
        viewModel.images.observe(viewLifecycleOwner) { images ->
            if(images!=null){
                val imageEventAdapter = ImageEventAdapter(requireContext(), images, viewModel = viewModel)
                binding.rvImageItem.adapter = imageEventAdapter
                val staggeredGridLayoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                binding.rvImageItem.layoutManager = staggeredGridLayoutManager
                binding.rvImageItem.setHasFixedSize(true)
            }
        }

        viewModel.events.observe(viewLifecycleOwner){ event ->
            if(event != null){
                //For runnable
                sliderDataList = event
                /*THIS IS THE CODE FOR AUTOMATIC SLIDER */
                viewPager = binding.viewPager
                val adapter = ImageEventAdapter(requireContext(), eventItems = event, isSlider = true, viewModel = viewModel){

                    val bundle = Bundle()
                    bundle.putInt("position", it) // Put the object into the bundle

                    val eventsFragment = EventsFragment()
                    eventsFragment.arguments = bundle

                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, eventsFragment, requireContext().getString(R.string.events_fragment_tag))
                        .addToBackStack(requireContext().getString(R.string.events_fragment_tag))
                        .commit()
                }
                viewPager.adapter = adapter
                createDots(binding.dotsLayout, sliderDataList.size)
            }
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
            //val intent = Intent(requireActivity(), PaymentActivity::class.java)
            val intent = Intent(requireActivity(), UpiPaymentActivity::class.java)
            startActivity(intent)
        }

        //Go to gallery fragment
        binding.llGoToGallery.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, GalleryFragment(), requireContext().getString(R.string.gallery_fragment_tag))
                .addToBackStack(requireContext().getString(R.string.gallery_fragment_tag))
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
                // Set initial size
                scaleX = 1f
                scaleY = 1f
            }
            dotsLayout.addView(dots[i])
        }

        dots[0]?.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dot_active
                )
            )
            animateDot(this, true)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { index, imageView ->
                    imageView?.let {
                        if(index == position){
                            it.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.dot_active
                                )
                            )
                            animateDot(it, true)
                        }else{
                            it.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.dot_inactive
                                )
                            )
                            animateDot(it, false)
                        }
                    }
                }

            }
        })
    }
    private fun animateDot(dot: ImageView, isActive: Boolean) {
        val scale = if (isActive) 1.5f else 1f // Adjust scale for active/inactive
        dot.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(200) // Animation duration in milliseconds
            .start()
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