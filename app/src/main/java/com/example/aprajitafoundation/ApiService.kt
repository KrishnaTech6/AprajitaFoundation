package com.example.aprajitafoundation

import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.model.ImageModel
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

data class UploadResponse(
    val message: String
)

data class DeleteResponse(
    val message: String
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
}
