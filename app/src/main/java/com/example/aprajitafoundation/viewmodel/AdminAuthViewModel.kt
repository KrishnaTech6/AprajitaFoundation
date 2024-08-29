package com.example.aprajitafoundation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.api.AuthResponse
import com.example.aprajitafoundation.api.GenericResponse
import com.example.aprajitafoundation.api.LoginRequest
import com.example.aprajitafoundation.api.LogoutRequest
import com.example.aprajitafoundation.api.ProfileUpdateRequest
import com.example.aprajitafoundation.api.RegisterRequest
import com.example.aprajitafoundation.api.RetrofitClient
import com.google.gson.Gson
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

    fun loginAdmin(request: LoginRequest, context: Context) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.loginAdmin(request)
                if (response.isSuccessful) {
                    _authResponse.value = response.body()
                    response.body()?.let { authResponse ->
                        val gson = Gson()
                        val userJson = gson.toJson(authResponse.user)
                        // Store token and user info in SharedPreferences
                        val sharedPreferences = context
                            .getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("token", authResponse.token)
                            putString("user", userJson) // saving the json object
                            apply()
                        }

                        Log.d("ViewModel", "${authResponse.token}, \n$userJson")
                    }
                } else {
                    _error.value = "Login failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("ViewModel", "$e")
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

    fun logout(context: Context) {
        viewModelScope.launch {
            _loading.value = true
            val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")
            Log.d("ViewModel", "token: "+token.toString())

            try {
                val response = apiService.logout(token.toString())
                if (response.isSuccessful) {
                    // Clear the token and user info from SharedPreferences
                    with(sharedPreferences.edit()) {
                        remove("token")
                        remove("user")  // Change "userId" to match how you're storing user data
                        apply()
                    }
                    _genericResponse.value = response.body()
                    Log.d("ViewModel","response: "+response.body().toString())
                } else {
                    _error.value = "Logout failed: ${response.message()}"
                    Log.d("ViewModel", response.message())
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("ViewModel","$e")
            } finally {
                _loading.value = false
            }
        }
    }

}