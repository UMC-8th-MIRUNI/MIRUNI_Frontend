package umcandroid.essential.miruni

data class LoginResponse (
    val errorCode: String?, // null 가능
    val message: String,
    val result: TokenResultLogin?
)

data class TokenResultLogin(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int
)