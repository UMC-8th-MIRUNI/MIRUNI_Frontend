package umcandroid.essential.miruni

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @POST("api/auth/normal")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}