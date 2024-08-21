package com.example.aprajitafoundation

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.model.MemberModel
import kotlinx.coroutines.launch
import retrofit2.Response

class DataViewModel : ViewModel() {

    private val _images = MutableLiveData<List<ImageModel>>()
    val images: LiveData<List<ImageModel>> get() = _images

    private val _events = MutableLiveData<List<EventModel>>()
    val events: LiveData<List<EventModel>> get() = _events

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _appTheme = MutableLiveData<String>()
    val appTheme: LiveData<String> get() = _appTheme

    private val _members = MutableLiveData<List<MemberModel>>()
    val members: LiveData<List<MemberModel>> get() = _members

    private val apiService = RetrofitClient.api

    fun fetchGalleryImages() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getGalleryImages()
                if (response.isSuccessful) {
                    _images.value = response.body()
                } else {
                    _error.value = "Error fetching images: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", "Error fetching gallery images", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchTeamMembers() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getTeamMembers()
                if (response.isSuccessful) {
                    _members.value = response.body()
                } else {
                    _error.value = "Error fetching members: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", "Error fetching member data", e)
            } finally {
                _loading.value = false
            }
        }
    }

    // ViewModel
    fun setAppTheme(theme: String) {
        _appTheme.value = theme
    }


    fun fetchAllEvents() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getAllEvents()
                if (response.isSuccessful) {
                    _events.value = response.body()
                } else {
                    _error.value = "Error fetching events: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", "Error fetching events", e)
            } finally {
                _loading.value = false
            }
        }
    }
}
