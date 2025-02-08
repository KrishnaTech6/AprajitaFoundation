package com.example.aprajitafoundation.model

// Data classes to represent requests and responses
data class RegisterRequest(var name: String, var email: String, var password: String, var profileImg: String?)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val message: String, var token: String, val user: User)
data class User(val id: String, var name: String, var email: String, var profileImg: String?)