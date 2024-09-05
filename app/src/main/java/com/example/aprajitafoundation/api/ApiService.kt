package com.example.aprajitafoundation.api

import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.model.MemberModel
import com.example.aprajitafoundation.model.MemberModel2
import com.example.aprajitafoundation.model.Payment
import com.example.aprajitafoundation.model.Socials
import com.example.aprajitafoundation.model.UserData
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface GalleryApi {
    @GET("get-gallery-images")
    suspend fun getGalleryImages(): Response<List<ImageModel>>  // to retrieve 15 images


    @POST("upload-gallery-image")
    suspend fun uploadGalleryImages(
        @Header("Authorization") token: String?,
        @Body images: ImagesRequest
    ): Response<GenericResponse>


    @DELETE("delete-gallery-image/{id}")
    suspend fun deleteGalleryImage(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Response<GenericResponse>


    @GET("get-all-gallery-images")
    suspend fun getAllGalleryImages(): Response<List<ImageModel>> // to retrieve all images


    @POST("add-team-member")
    suspend fun addTeamMember(
        @Header("Authorization") token: String?,
        @Body member: MemberModel2?,
    ): Response<GenericResponse>

    @PUT("update-team-member/{id}")
    suspend fun updateTeamMember(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
        @Body member: MemberModel2?
    ): Response<GenericResponse>

    @GET("get-all-team-members")
    suspend fun getTeamMembers(): Response <List <MemberModel>>
    @DELETE("delete-team-member/{id}")
    suspend fun deleteMember(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Response<GenericResponse>

    @GET("get-events")
    suspend fun getAllEvents(): Response<List<EventModel>>

    @POST("add-event")
    suspend fun addEvent(
        @Header("Authorization") token: String?,
        @Body event: EventModel?
    ): Response<GenericResponse>

    @PUT("update-event/{id}")
    suspend fun updateEvent(
        @Header("Authorization") token: String?,
        @Path("id") id: String?,
        @Body event: EventModel?
    ): Response<GenericResponse>

    @DELETE("delete-event/{id}")
    suspend fun deleteEvent(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Response<GenericResponse>



    @POST("create-payment")
    suspend fun createPayment(@Body paymentRequest: PaymentRequest): Response<PaymentResponse>

    @POST("google-login")
    suspend fun sendUserData(@Body userData: UserData): Response<GenericResponse>

    @POST("verify-payment")
    suspend fun storeVerifiedPayment(@Body payment: Payment): Response<GenericResponse>

    @GET("get-all-payment-data")
    suspend fun getAllPayments(@Header("Authorization") token: String?):Response<PaymentDetailResponse>

}

data class ImagesRequest(
    val images: List<String>
)
data class GenericResponse(val message: String)
data class PaymentRequest(val amount: Double)
data class PaymentResponse(val order: Order)

data class PaymentDetailResponse(val payments: List<Payment>)
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
