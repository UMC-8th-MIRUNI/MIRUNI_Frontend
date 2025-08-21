package umcandroid.essential.miruni

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {
    @POST("/api/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @POST("api/auth/login/normal")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/api/auth/login/kakao")
    suspend fun loginWithKakao(
        @Body request: KakaoLoginRequest
    ): Response<KakaoLoginResponse>

    @POST("api/auth/survey")
    suspend fun sendSurvey(
        @Header("Authorization") token: String,
        @Body body: SurveyRequest
    ): Response<SurveyResponse>

    @POST("/api/auth/password/reset")
    suspend fun sendVerificationCode(
        @Body request: ResetPwdRequest
    ): Response<ResetPwdResponse>

    @POST("/api/auth/password/reset/verification")
    suspend fun verifyCode(
        @Body request: VerifyCodeRequest
    ): Response<VerifyCodeResponse>

    @PATCH("/api/auth/password/reset")
    suspend fun completeResetPassword(
        @Header("Reset-Token") resetToken: String,
        @Body request: ResetPwdCompleteRequest
    ): Response<ResetPwdCompleteResponse>

    @POST("/api/auth/login/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequest
    ): Response<GoogleLoginResponse>

    @PATCH("/api/auth/kakao")
    suspend fun kakaoSignup(
        @Header("Authorization") token: String,
        @Body request: KakaoSignupRequest
    ): Response<KakaoSignupResponse>

    @POST("/api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>

    @DELETE("/api/auth")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): Response<DeleteAccountResponse>
}
