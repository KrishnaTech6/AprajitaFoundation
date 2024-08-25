package com.example.aprajitafoundation.model


data class UserData(
    val googleId : String?,
    val email: String?,
    val name: String?,
    val picture: String?,
)

data class GoogleIDToken(val idToken: GoogleIDToken)