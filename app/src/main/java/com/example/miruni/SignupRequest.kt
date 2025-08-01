package com.example.miruni

data class SignupRequest(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val nickname: String,
    val userPreference: String
)
