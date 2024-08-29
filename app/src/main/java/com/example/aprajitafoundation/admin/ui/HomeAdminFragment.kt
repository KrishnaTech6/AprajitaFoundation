package com.example.aprajitafoundation.admin.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aprajitafoundation.api.User
import com.example.aprajitafoundation.databinding.FragmentHome2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeAdminFragment : Fragment() {

    private var _binding: FragmentHome2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHome2Binding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences",MODE_PRIVATE )

        val savedUserJson = sharedPreferences.getString("user", null)
        val type = object : TypeToken<User>() {}.type
        val savedUser = Gson().fromJson<User>(savedUserJson, type)


        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()) //Monday , 29 August 2024
        val date = dateFormat.format(System.currentTimeMillis())

        binding.welcomeText.text = "Welcome, ${savedUser.name}!"
        binding.dateTimeText.text = "Today is $date"

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}