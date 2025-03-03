package com.example.aprajitafoundation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.api.RetrofitClient
import com.example.aprajitafoundation.api.SingleLiveEvent
import com.example.aprajitafoundation.model.AuthResponse
import com.example.aprajitafoundation.model.GenericResponse
import com.example.aprajitafoundation.model.LoginRequest
import com.example.aprajitafoundation.model.RegisterRequest
import com.example.aprajitafoundation.model.User
import com.google.gson.Gson
import kotlinx.coroutines.launch


class AdminAuthViewModel(application: Application) : AndroidViewModel(application ) {

    private val _authResponse = SingleLiveEvent<AuthResponse>()
    val authResponse: LiveData<AuthResponse> get() = _authResponse
    private val _authResponseLogin = MutableLiveData<AuthResponse>()
    val authResponseLogin: LiveData<AuthResponse> get() = _authResponseLogin

    private val _genericResponse = SingleLiveEvent<GenericResponse>()
    val genericResponse: LiveData<GenericResponse> get() = _genericResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val apiService = RetrofitClient.getAdminAuthApi(application)

    private fun getToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.apppreferences), Context.MODE_PRIVATE)
        return sharedPreferences.getString(context.getString(R.string.token_login_admin), "") ?: ""
    }

    fun registerAdmin(context: Context , request: RegisterRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.registerAdmin(getToken(context), request)
                // Check if the response came from the cache
                if (response.raw().cacheResponse != null) {
                    // Response is from cache
                    _loading.value = false
                }
                if (response.isSuccessful) {
                    _genericResponse.postValue(response.body())
                } else {
                    _error.value = "Registration failed: ${response.message()}"
                    Log.d("Register", "${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loginAdmin(request: LoginRequest, context: Context) {
        Log.d("TAG", request.toString())
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.loginAdmin(request)
                if (response.raw().cacheResponse != null) {
                    _loading.value = false
                }
                if (response.isSuccessful) {
                    _authResponseLogin.value = response.body()
                    response.body()?.let { authResponse ->
                        val userJson = Gson().toJson(authResponse.user)
                        // Store token and user info in SharedPreferences
                        val sharedPreferences = context
                            .getSharedPreferences(context.getString(R.string.apppreferences), Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString(context.getString(R.string.token_login_admin), authResponse.token)
                            putString(context.getString(R.string.user_data_admin), userJson) // saving the json object
                            apply()
                        }

                        Log.d("ViewModel", "${authResponse.token}, \n$userJson")
                    }
                } else {
                    _error.value = "Login failed: ${response.message()}"
                    Log.d("TAG", "${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("ViewModel", "$e")
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(context: Context, request: User) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.updateProfile(getToken(context), request)
                if (response.raw().cacheResponse != null) {
                    _loading.value = false
                }
                if (response.isSuccessful) {
                    _genericResponse.postValue(response.body())
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

    fun fetchProfile(context: Context) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val sharedPreferences = context.getSharedPreferences(context.getString(R.string.apppreferences), Context.MODE_PRIVATE)
                val response = apiService.fetchProfile(getToken(context))
                if (response.raw().cacheResponse != null) {
                    _loading.value = false
                }
                if (response.isSuccessful) {
                    _authResponse.postValue(response.body())
                    response.body()?.let { authResponse ->
                        val gson = Gson()
                        val userJson = gson.toJson(authResponse.user)
                        // Store token and user info in SharedPreferences
                        with(sharedPreferences.edit()) {
                            putString(context.getString(R.string.user_data_admin), userJson) // saving the json object
                            apply()
                        }

                        Log.d("ViewModel", userJson)
                    }
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
            val sharedPreferences = context.getSharedPreferences(context.getString(R.string.apppreferences), Context.MODE_PRIVATE)
            try {
                val response = apiService.logout(getToken(context))
                if (response.raw().cacheResponse != null) {
                    _loading.value = false
                }
                if (response.isSuccessful) {
                    // Clear the token and user info from SharedPreferences
                    with(sharedPreferences.edit()) {
                        remove(context.getString(R.string.token_login_admin))
                        remove(context.getString(R.string.user_data_admin))  // Change "userId" to match how you're storing user data
                        apply()
                    }
                    _genericResponse.postValue(response.body())
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

    fun resetAuthResponse() {
        _authResponse.call()
    }
    fun resetGenericResponse() {
        _genericResponse.call()
    }
}
