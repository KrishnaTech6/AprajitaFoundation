package com.example.aprajitafoundation.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaMetadata
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.activities.PaymentActivity
import com.example.aprajitafoundation.adapter.ImageAdapter
import com.example.aprajitafoundation.adapter.ImageEventAdapter
import com.example.aprajitafoundation.adapter.SliderAdapter
import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.data.DataSource
import com.example.aprajitafoundation.databinding.FragmentDashboardBinding
import com.example.aprajitafoundation.model.NameItem

class DashboardFragment : BaseFragment() , ImageAdapter.ItemClickListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var binding: FragmentDashboardBinding

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val sliderDataList = DataSource().loadSliderData()

    val imageItem = DataSource().loadNameData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentDashboardBinding.inflate(layoutInflater)

        /*THIS IS THE CODE FOR AUTOMATIC SLIDER */
        viewPager = binding.viewPager
        sliderAdapter = SliderAdapter(requireContext(), sliderDataList)
        viewPager.adapter = sliderAdapter
        // Initialize the runnable for automatic sliding
        runnable = Runnable {
            val nextItem =
                if (viewPager.currentItem == sliderDataList.size - 1) 0 else viewPager.currentItem + 1
            viewPager.currentItem = nextItem
            handler.postDelayed(runnable, 2500) // Delay in milliseconds between slides
        }
        createDots(binding.dotsLayout, sliderDataList.size) //to show dots below slider

        /*THIS IS THE CODE FOR NAME, IMAGE, DESIGNATION  RECYCLERVIEW */
        val imageItem = DataSource().loadNameData()
        binding.rvItems.adapter = ImageAdapter(requireContext(), imageItem, this)
        binding.rvItems.setHasFixedSize(true)

        val imageItem2 = DataSource().loadImageData2()
        binding.rvImageItem.adapter = ImageEventAdapter(requireContext(), imageItem2)
        binding.rvImageItem.setHasFixedSize(true)

        //new Recycler view presentation in main layout
//        recyclerItemView(imageItem)


        //To chat with
        binding.llWtspContact.setOnClickListener {
            openWhatsApp(Constants.phnNumber)
        }

        binding.llDonate.setOnClickListener {
            val intent=  Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }
        binding.llShare.setOnClickListener {
            shareLink(Constants.apkLink)
        }

        binding.llEmail.setOnClickListener {
            openEmail(Constants.email)
        }


        return binding.root

    }

    private fun recyclerItemView(item: List<NameItem>){
        val newRecyclerView: RecyclerView = RecyclerView(requireContext())
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        newRecyclerView.layoutManager = layoutManager
        newRecyclerView.adapter = ImageAdapter(requireContext(), item, this)
        newRecyclerView.setHasFixedSize(true)

        val layoutParams= ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 10, 0, 0 )
        newRecyclerView.layoutParams = layoutParams
        val parentLayout: ViewGroup  = binding.llScroll
        parentLayout.addView(newRecyclerView)

    }

    private fun createDots(dotsLayout: LinearLayout, size: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(4, 2, 4, 2)

        for (i in 0 until size) {
            dots[i] = ImageView(requireContext())
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dot_inactive
                )
            )
            dots[i]?.layoutParams = params
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
                for (i in 0 until size) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.dot_inactive
                        )
                    )
                }

                dots[position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.dot_active
                    )
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 2500)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onItemClick(nameItem: NameItem) {
        val memberFragment = MemberFragment.newInstance(nameItem)
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, memberFragment)
            .addToBackStack(null) // Add the transaction to the back stack
            .commit()
    }

}