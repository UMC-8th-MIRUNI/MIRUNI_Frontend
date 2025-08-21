package com.example.miruni

data class KakaoSignupResponse(
    val errorCode: String?,
    val message: String,
    val result: TokenResult
) {
    data class TokenResult(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String,
        val accessTokenExpiresIn: Long,
        val refreshTokenExpiresIn: Long
    )
}
