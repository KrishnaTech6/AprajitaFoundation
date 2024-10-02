package com.example.aprajitafoundation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.api.GenericResponse
import com.example.aprajitafoundation.api.ImagesRequest
import com.example.aprajitafoundation.api.PaymentDetailResponse
import com.example.aprajitafoundation.api.PaymentRequest
import com.example.aprajitafoundation.api.RetrofitClient
import com.example.aprajitafoundation.model.*
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {

    // LiveData objects for observing data in the UI
    val images: LiveData<List<ImageModel>> get() = _images
    val allImages: LiveData<List<ImageModel>> get() = _allImages
    val events: LiveData<List<EventModel>> get() = _events
    val members: LiveData<List<MemberModel>> get() = _members
    val orderId: LiveData<String> get() = _orderId
    val paymentVerificationStatus: LiveData<String> get() = _paymentVerificationStatus
    val allPayments: LiveData<PaymentDetailResponse> get() = _allPayments
    val deleteResponse: LiveData<GenericResponse> get() = _deleteResponse
    val updateResponse: LiveData<GenericResponse> get() = _updateResponse
    val uploadResponse: LiveData<GenericResponse> get() = _uploadResponse
    val loading: LiveData<Boolean> get() = _loading
    val error: LiveData<String> get() = _error

    // Private MutableLiveData for internal use
    private val _images = MutableLiveData<List<ImageModel>>()
    private val _allImages = MutableLiveData<List<ImageModel>>()
    private val _events = MutableLiveData<List<EventModel>>()
    private val _members = MutableLiveData<List<MemberModel>>()
    private val _orderId = MutableLiveData<String>()
    private val _paymentVerificationStatus = MutableLiveData<String>()
    private val _allPayments = MutableLiveData<PaymentDetailResponse>()
    private val _deleteResponse = MutableLiveData<GenericResponse>()
    private val _updateResponse = MutableLiveData<GenericResponse>()
    private val _uploadResponse = MutableLiveData<GenericResponse>()
    private val _loading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<String>()

    private val apiService = RetrofitClient.api

    // Simplified API call methods
    fun fetchGalleryImages() = makeApiCall(apiService::getGalleryImages, _images)
    fun fetchAllGalleryImages() = makeApiCall(apiService::getAllGalleryImages, _allImages)
    fun fetchTeamMembers() = makeApiCall(apiService::getTeamMembers, _members)
    fun fetchAllEvents() = makeApiCall(apiService::getAllEvents, _events)

    fun createPayment(amount: Double) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.createPayment(PaymentRequest(amount))
                if (response.isSuccessful) {
                    _orderId.value = response.body()?.order?.id
                } else {
                    handleError("Error creating payment", response.message())
                }
            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun sendUserData(userData: UserData) = makeApiCall({ apiService.sendUserData(userData) }, null)

    fun verifyPayment(payment: Payment) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.storeVerifiedPayment(payment)
                if (response.isSuccessful) {
                    _paymentVerificationStatus.value = "Payment Verified Successfully"
                } else {
                    _error.value = "Error verifying payment: ${response.message()}"
                    Log.e("DataViewModel", "Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", e.message, e)
            } finally {
                _loading.value = false
            }
        }
    }


    fun getAllPayments(context: Context) =
        makeApiCallWithToken(context, apiService::getAllPayments, _allPayments)

    fun deleteGalleryImage(context: Context, imageId: String?) = makeApiCallWithToken(
        context,
        { apiService.deleteGalleryImage(it, imageId) },
        _deleteResponse
    )

    fun deleteEvent(context: Context, eventId: String?) =
        makeApiCallWithToken(context, { apiService.deleteEvent(it, eventId) }, _deleteResponse)

    fun deleteMember(context: Context, memberId: String?) =
        makeApiCallWithToken(context, { apiService.deleteMember(it, memberId) }, _deleteResponse)


    fun addEvent(context: Context, event: EventModel?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    .getString("token", "") ?: ""

                Log.d("ViewModel", "$token")
                val response = apiService.addEvent(token, event)
                if (response.isSuccessful) {
                    _uploadResponse.value = response.body()
                } else {
                    handleError("Error fetching response", response.message())
                }

            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMember(context: Context, member: MemberModel2?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    .getString("token", "") ?: ""
                val response = apiService.addTeamMember(token, member)
                if (response.isSuccessful) {
                    _uploadResponse.value = response.body()
                } else {
                    handleError("Error fetching response", response.message())
                }

            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateEvent(context: Context, eventModel: EventModel?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    .getString("token", "") ?: ""
                val response = apiService.updateEvent(token, eventModel?.id, eventModel)
                Log.d("ViewModel", "$token")
                if (response.isSuccessful) {
                    _updateResponse.value = response.body()
                } else {
                    handleError("Error fetching response", response.message())
                }

            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTeamMember(context: Context, teamMember: MemberModel2?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    .getString("token", "") ?: ""
                val response = apiService.updateTeamMember(token, teamMember?.id, teamMember)
                if (response.isSuccessful) {
                    _updateResponse.value = response.body()
                } else {
                    handleError("Error fetching response", response.message())
                }

            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    private fun <T> makeApiCall(
        apiCall: suspend () -> retrofit2.Response<T>,
        liveData: MutableLiveData<T>?,
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    liveData?.value = response.body()
                } else {
                    handleError("Error fetching data", response.message())
                }
            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    private fun <T> makeApiCallWithToken(
        context: Context,
        apiCall: suspend (String) -> retrofit2.Response<T>,
        liveData: MutableLiveData<T>,
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val token = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    .getString("token", "") ?: ""
                val response = apiCall(token)
                if (response.isSuccessful) {
                    liveData.value = response.body()
                } else {
                    handleError("Error fetching data", response.message())
                }
            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    private fun handleError(message: String, details: String? = null) {
        _error.value = "$message: $details"
        Log.e("DataViewModel", "$message: $details")
    }

    fun uploadGalleryImages(context: Context, images: ImagesRequest) {
        viewModelScope.launch {
            val token = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                .getString("token", "") ?: ""

            _loading.value = true
            try {
                val response = apiService.uploadGalleryImages(token, images)
//                val jsonString = Gson().toJson(images)
//                Log.d("RequestBody", jsonString)
                if (response.isSuccessful) {
                    // Handle success for each file
                    _uploadResponse.value = response.body()
                } else {
                    handleError("Error uploading image", response.message())

                    Log.d("ViewModel" , response.errorBody()?.string() ?: "Error body empty")
                }
            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

}
