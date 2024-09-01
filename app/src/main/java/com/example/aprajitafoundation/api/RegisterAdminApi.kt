package com.example.aprajitafoundation.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Data classes to represent requests and responses
data class RegisterRequest(var name: String, var email: String, var password: String, var profileImg: String?)
data class LoginRequest(val email: String, val password: String)
data class ProfileUpdateRequest(val name: String, val email: String, val profileImg: String?)
data class AuthResponse(val message: String, var token: String, val user: User)

data class User(val id: String, val name: String, val email: String, val profileImg: String?)

interface AuthApi {

    @POST("register-admin")
    suspend fun registerAdmin(@Header("Authorization") token: String, @Body request: RegisterRequest): Response<GenericResponse>

    @POST("login-admin")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<AuthResponse>

    @POST("update-adminProfile")
    suspend fun updateProfile(@Header("Authorization") token: String, @Body request: ProfileUpdateRequest): Response<GenericResponse>

    @POST("admin-profile")
    suspend fun fetchProfile(): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<GenericResponse>
}