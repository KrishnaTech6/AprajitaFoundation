package com.example.aprajitafoundation.api

import com.example.aprajitafoundation.utility.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = Constants.serverUrl

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: UserAdminApi by lazy {
        retrofit.create(UserAdminApi::class.java)
    }

    val adminAuthApi: AdminAuthApi by lazy {
        retrofit.create(AdminAuthApi::class.java)
    }
}
