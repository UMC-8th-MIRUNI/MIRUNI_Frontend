package umcandroid.essential.miruni

data class GoogleLoginResponse(
    val errorCode: String?,
    val message: String,
    val result: ResultData?
) {
    data class ResultData(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String,
        val accessTokenExpiresIn: Long,
        val refreshTokenExpiresIn: Long,
        val isNewUser: Boolean,
        val isPending: Boolean
    )
}
