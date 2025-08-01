package com.example.miruni

data class SignupResponse(
    val errorCode: String?, // null 가능
    val message: String,
    val result: TokenResult?
)

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val accessTokenExpiresIn: Int,
    val refreshTokenExpiresIn: Int
)
