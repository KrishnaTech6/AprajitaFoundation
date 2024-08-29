package com.example.aprajitafoundation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aprajitafoundation.api.GenericResponse
import com.example.aprajitafoundation.api.PaymentDetailResponse
import com.example.aprajitafoundation.api.PaymentRequest
import com.example.aprajitafoundation.api.PaymentResponse
import com.example.aprajitafoundation.api.RetrofitClient
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.model.UserData
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {

    private val _images = MutableLiveData<List<ImageModel>>()
    val images: LiveData<List<ImageModel>> get() = _images

    private val _allImages = MutableLiveData<List<ImageModel>>()
    val allImages: LiveData<List<ImageModel>> get() = _allImages

    private val _events = MutableLiveData<List<EventModel>>()
    val events: LiveData<List<EventModel>> get() = _events

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _members = MutableLiveData<List<MemberModel>>()
    val members: LiveData<List<MemberModel>> get() = _members


    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String> get() = _orderId

    private val _paymentVerificationStatus = MutableLiveData<String>()
    val paymentVerificationStatus: LiveData<String> get() = _paymentVerificationStatus

    private val _allPayments = MutableLiveData<PaymentDetailResponse>()
    val allPayments: LiveData<PaymentDetailResponse> get() =_allPayments


    private val _deleteResponse = MutableLiveData<GenericResponse>()
    val deleteResponse: LiveData<GenericResponse> get() =_deleteResponse

    private val apiService = RetrofitClient.api

    fun fetchGalleryImages() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getGalleryImages()
                if (response.isSuccessful) {
                    _images.value = response.body()
                    Log.d("DataViewModel", "${response.body()}")

                } else {
                    _error.value = "Error fetching images: ${response.message()}"
                    Log.d("DataViewModel", "${response.message()}")
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", "Error fetching gallery images", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchAllGalleryImages() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getAllGalleryImages()
                if (response.isSuccessful) {
                    _allImages.value = response.body()
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

    fun createPayment(amount: Double) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val paymentRequest = PaymentRequest(amount)
                val response = apiService.createPayment(paymentRequest)
                if (response.isSuccessful) {
                    val order = response.body()?.order
                    order?.let {
                        _orderId.value = it.id  // Store the order ID
                    }
                } else {
                    _error.value = "Error creating payment: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", "Error creating payment", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun sendUserData(userData:UserData) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.sendUserData(userData)
                if (response.isSuccessful) {
                    _error.value = "User data submitted successfully"
                } else {
                    _error.value = "Error submitting user data: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", "Error submitting user data", e)
            } finally {
                _loading.value = false
            }
        }
    }

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

    fun getAllPayments(context: Context){
        viewModelScope.launch {
            _loading.value=true
            try {
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")
                Log.d("ViewModel", "token: "+token.toString())
                val response =apiService.getAllPayments(token)
                if (response.isSuccessful) {
                    _allPayments.value = response.body()
                } else {
                    _error.value = "Error fetching payments: ${response.message()}"
                    Log.e("DataViewModel", "Response: ${response.errorBody()?.string()}")
                }
            }catch (e:Exception){
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", e.message, e)
            }finally {
                _loading.value = false
            }
        }
    }

    fun deleteGalleryImage( context:Context, imageId:String?){
        viewModelScope.launch {
            try {
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")
                Log.d("ViewModel", "token: "+token.toString())

                val response =apiService.deleteGalleryImage(token, imageId)
                if (response.isSuccessful) {
                    _deleteResponse.value =response.body()
                    Log.d("DataViewModel", "${response.body()}")
                } else {
                    _error.value = "Error Deleting image: ${response.message()}"
                    Log.e("DataViewModel", "Response: ${response.errorBody()?.string()}")
                }

            }catch (e:Exception){
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", e.message, e)
            }
            finally {
                _loading.value = false
            }
        }
    }

    fun deleteEvent( context:Context, eventId:String?){
        viewModelScope.launch {
            try {
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")
                Log.d("ViewModel", "token: "+token.toString())

                val response =apiService.deleteEvent(token, eventId)
                if (response.isSuccessful) {
                    _deleteResponse.value =response.body()
                    Log.d("DataViewModel", "${response.body()}")
                } else {
                    _error.value = "Error Deleting Event: ${response.message()}"
                    Log.e("DataViewModel", "Response: ${response.errorBody()?.string()}")
                }

            }catch (e:Exception){
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", e.message, e)
            }
            finally {
                _loading.value = false
            }
        }
    }

    fun deleteMember(context: Context, memberId: String?){

        viewModelScope.launch {
            try {
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")
                Log.d("ViewModel", "token: "+token.toString())

                val response =apiService.deleteMember(token, memberId)
                if (response.isSuccessful) {
                    _deleteResponse.value =response.body()
                    Log.d("DataViewModel", "${response.body()}")
                } else {
                    _error.value = "Error Deleting Member: ${response.message()}"
                    Log.e("DataViewModel", "Response: ${response.errorBody()?.string()}")
                }

            }catch (e:Exception){
                _error.value = "Exception: ${e.message}"
                Log.e("DataViewModel", e.message, e)
            }
            finally {
                _loading.value = false
            }
        }

    }
}
