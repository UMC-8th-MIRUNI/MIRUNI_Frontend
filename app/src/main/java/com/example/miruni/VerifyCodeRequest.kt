package com.example.miruni

data class VerifyCodeRequest(
    val email: String,
    val code: String
)
