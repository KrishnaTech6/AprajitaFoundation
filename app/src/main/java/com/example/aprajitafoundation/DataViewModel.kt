package com.example.aprajitafoundation

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.model.ImageModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response

class DataViewModel:ViewModel() {

    private val _images = MutableLiveData<List<ImageModel>>()
    val images: LiveData<List<ImageModel>> get() = _images

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val apiService = RetrofitClient.api

    fun fetchGalleryImages() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getGalleryImages()
                if (response.isSuccessful) {
                    _images.value = response.body()
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle error
                Log.e(this@DataViewModel.toString(), e.toString(), e)
            } finally {
                _loading.value = false
            }
        }
    }
}