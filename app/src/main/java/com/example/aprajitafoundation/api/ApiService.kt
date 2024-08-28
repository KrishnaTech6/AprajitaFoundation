package com.example.aprajitafoundation.api

import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.model.UserData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GalleryApi {
    @GET("get-gallery-images")
    suspend fun getGalleryImages(): Response<List<ImageModel>>  // to retrieve 15 images

    @GET("get-all-gallery-images")
    suspend fun getAllGalleryImages(): Response<List<ImageModel>> // to retrieve all images

    @GET("get-team-members")
    suspend fun getTeamMembers(): Response <List <MemberModel>>

    @GET("get-events")
    suspend fun getAllEvents(): Response<List<EventModel>>

    @POST("create-payment")
    suspend fun createPayment(@Body paymentRequest: PaymentRequest): Response<PaymentResponse>

    @POST("google-login")
    suspend fun sendUserData(@Body userData: UserData): Response<GenericResponse>

    @POST("verify-payment")
    suspend fun storeVerifiedPayment(@Body payment: Payment): Response<GenericResponse>

}
data class GenericResponse(val message: String)
data class PaymentRequest(val amount: Double)
data class PaymentResponse(val order: Order)
data class Order(
    val id: String,
    val amount: Int,
    val currency: String,
    val receipt: String?,
    val status: String?
)

object RetrofitClient {
    private const val BASE_URL = Constants.serverUrl

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GalleryApi by lazy {
        retrofit.create(GalleryApi::class.java)
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}
