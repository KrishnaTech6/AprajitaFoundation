package com.example.aprajitafoundation

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface GalleryApi {
    @GET("get-gallery-images")
    suspend fun getGalleryImages(): Response<List<ImageModel>>

    @Multipart
    @POST("upload-gallery-image")
    suspend fun uploadGalleryImage(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @DELETE("delete-gallery-image/{id}")
    suspend fun deleteGalleryImage(
        @Path("id") id: String
    ): Response<DeleteResponse>
}

data class ImageModel(
    val _id: String,
    val image: String
)

data class UploadResponse(
    val message: String
)

data class DeleteResponse(
    val message: String
)

object RetrofitClient {
    private const val BASE_URL = "http://your-backend-url.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GalleryApi by lazy {
        retrofit.create(GalleryApi::class.java)
    }
}
//
//import com.example.aprajitafoundation.data.Constants
//import com.example.aprajitafoundation.model.MemberItem
//import com.google.android.gms.common.api.Response
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.POST
//
//class ApiService {
//
//    interface ApiService {
//        @GET("members")
//        suspend fun getMembers(): Response<List<MemberItem>>
//
//        @POST("members")
//        suspend fun addMember(@Body memberItem: MemberItem): Response<MemberItem>
//    }
//
//    // Retrofit setup
//    val retrofit = Retrofit.Builder()
//        .baseUrl(Constants.serverUrl)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val apiService = retrofit.create(ApiService::class.java)
//
//    // Fetch members
//    fun fetchMembers() {
//        GlobalScope.launch {
//            val response = apiService.getMembers()
//            if (response.isSuccessful) {
//                val members = response.body()
//                // Handle the member list
//            }
//        }
//    }
//
//    // Add a new member
//    fun addMember(member: MemberItem) {
//        GlobalScope.launch {
//            val response = apiService.addMember(member)
//            if (response.isSuccessful) {
//                val newMember = response.body()
//                // Handle the new member
//            }
//        }
//    }
//}