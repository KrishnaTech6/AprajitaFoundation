package com.example.aprajitafoundation.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Data classes to represent requests and responses
data class RegisterRequest(val name: String, val email: String, val password: String, val profileImg: String?)
data class LoginRequest(val email: String, val password: String)
data class ProfileUpdateRequest(val name: String, val email: String, val profileImg: String?)
data class AuthResponse(val message: String, var token: String, val user: User)

data class LogoutRequest(val token: String?)

data class User(val id: String, val name: String, val email: String, val profileImg: String?)

interface AuthApi {

    @POST("register-admin")
    suspend fun registerAdmin(@Body request: RegisterRequest): Response<GenericResponse>

    @POST("login-admin")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<AuthResponse>

    @POST("update-adminProfile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<GenericResponse>

    @POST("admin-profile")
    suspend fun fetchProfile(): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<GenericResponse>
}