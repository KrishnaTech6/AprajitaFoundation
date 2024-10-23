package com.example.aprajitafoundation.api

import android.content.Context
import com.example.aprajitafoundation.utility.Constants
import com.example.aprajitafoundation.utility.isInternetAvailable
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitClient {
    private const val BASE_URL = Constants.serverUrl

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val cacheSize: Long= 5 * 1024 * 1024 // 5 MB cache size
        val cacheDir = File(context.cacheDir, "http_cache") // Cache directory
        val cache = Cache(cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(cache) // Set the cache for OkHttpClient
            .addInterceptor { chain ->
                var request = chain.request()

                // Use cache when offline or fresh data for 5 minutes when online
                request = if (isInternetAvailable(context))
                    request.newBuilder().header("Cache-Control", "public, max-age=300").build() //300 seconds
                else
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=604800").build() // Cache for 1 week when offline

                chain.proceed(request)
            }
            .build()
    }

    private fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .client(getOkHttpClient(context)) // Use OkHttpClient with caching
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Publicly available Retrofit API
    fun getApi(context: Context): UserAdminApi {
        return getRetrofit(context).create(UserAdminApi::class.java)
    }

    fun getAdminAuthApi(context: Context): AdminAuthApi {
        return getRetrofit(context).create(AdminAuthApi::class.java)
    }
}
