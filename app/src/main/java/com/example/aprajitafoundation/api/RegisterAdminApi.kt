package com.example.aprajitafoundation.api

import com.example.aprajitafoundation.model.AuthResponse
import com.example.aprajitafoundation.model.GenericResponse
import com.example.aprajitafoundation.model.LoginRequest
import com.example.aprajitafoundation.model.RegisterRequest
import com.example.aprajitafoundation.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AdminAuthApi {

    @POST("register-admin")
    suspend fun registerAdmin(@Header("Authorization") token: String, @Body request: RegisterRequest): Response<GenericResponse>

    @POST("login-admin")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<AuthResponse>

    @POST("update-adminProfile")
    suspend fun updateProfile(@Header("Authorization") token: String, @Body request: User): Response<GenericResponse>

    @POST("admin-profile")
    suspend fun fetchProfile(@Header("Authorization") token:String?): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<GenericResponse>
}