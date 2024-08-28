package com.example.aprajitafoundation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.api.AuthResponse
import com.example.aprajitafoundation.api.GenericResponse
import com.example.aprajitafoundation.api.LoginRequest
import com.example.aprajitafoundation.api.ProfileUpdateRequest
import com.example.aprajitafoundation.api.RegisterRequest
import com.example.aprajitafoundation.api.RetrofitClient
import kotlinx.coroutines.launch

class AdminAuthViewModel : ViewModel() {

    private val _authResponse = MutableLiveData<AuthResponse>()
    val authResponse: LiveData<AuthResponse> get() = _authResponse

    private val _genericResponse = MutableLiveData<GenericResponse>()
    val genericResponse: LiveData<GenericResponse> get() = _genericResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val apiService = RetrofitClient.authApi

    fun registerAdmin(request: RegisterRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.registerAdmin(request)
                if (response.isSuccessful) {
                    _genericResponse.value = response.body()
                } else {
                    _error.value = "Registration failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loginAdmin(request: LoginRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.loginAdmin(request)
                if (response.isSuccessful) {
                    _authResponse.value = response.body()
                } else {
                    _error.value = "Login failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(request: ProfileUpdateRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.updateProfile(request)
                if (response.isSuccessful) {
                    _genericResponse.value = response.body()
                } else {
                    _error.value = "Update failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.fetchProfile()
                if (response.isSuccessful) {
                    _authResponse.value = response.body()
                } else {
                    _error.value = "Profile fetch failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.logout()
                if (response.isSuccessful) {
                    _genericResponse.value = response.body()
                } else {
                    _error.value = "Logout failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
