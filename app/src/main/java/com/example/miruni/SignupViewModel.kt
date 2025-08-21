package com.example.miruni

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SignupViewModel : ViewModel() {

    // 사용자 입력값 저장
    val name = MutableLiveData<String>()
    val birthday = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    var nickname = MutableLiveData<String>()

    val agreedPrivacyPolicy = MutableLiveData<Boolean>(false)

    // 서버 응답 결과 처리용
    private val _signupResult = MutableLiveData<Result<SignupResponse>>()
    val signupResult: LiveData<Result<SignupResponse>> = _signupResult

    private val _kakaoSignupResult = MutableLiveData<Result<KakaoSignupResponse>>()
    val kakaoSignupResult: LiveData<Result<KakaoSignupResponse>> = _kakaoSignupResult

    // 일반 회원가입
    fun signup() {
        val request = SignupRequest(
            name = name.value ?: "",
            birthday = birthday.value ?: "",
            email = email.value ?: "",
            phoneNumber = phoneNumber.value ?: "",
            password = password.value ?: "",
            nickname = nickname.value ?: ""
        )
        viewModelScope.launch {
            try {
                Log.d("Signup", "요청 시작: $request")
                val response = RetrofitInstance.authService.signup(request)
                Log.d("Signup", "응답 코드: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    Log.d("Signup", "응답 바디: ${response.body()}")
                    _signupResult.value = Result.success(response.body()!!)
                } else {
                    val errorBodyStr = response.errorBody()?.string()
                    Log.e("Signup", "회원가입 실패: ${response.code()} - $errorBodyStr")
                    _signupResult.value = Result.failure(Exception(errorBodyStr ?: "알 수 없는 오류"))
                }

            } catch (e: IOException) {
                Log.e("Signup", "네트워크 오류 발생", e)
                _signupResult.value = Result.failure(Exception("네트워크 오류 발생"))
            } catch (e: HttpException) {
                Log.e("Signup", "서버 오류", e)
                _signupResult.value = Result.failure(Exception("서버 오류: ${e.message()}"))
            } catch (e: Exception) {
                Log.e("Signup", "기타 오류", e)
                _signupResult.value = Result.failure(e)
            }
        }
    }

    // SignupFragment2에서 전달받은 kakaoToken을 ViewModel에 전달했다고 가정
    fun kakaoSignup(kakaoAccessToken: String) {
        viewModelScope.launch {
            try {
                val request = KakaoSignupRequest(
                    name = name.value ?: "",
                    birthday = birthday.value ?: "",
                    phoneNumber = phoneNumber.value ?: "",
                    agreedPrivacyPolicy = agreedPrivacyPolicy.value ?: false,
                    nickname = nickname.value ?: ""
                )
                Log.d("KakaoSignup", "보낼 요청 데이터: $request")
                Log.d("KakaoSignup", "카카오 accessToken: $kakaoAccessToken")

                val response = RetrofitInstance.authService.kakaoSignup(
                    token = "Bearer $kakaoAccessToken", // ✅ 카카오 로그인에서 받은 accessToken
                    request = request
                )
                Log.d("KakaoSignup", "서버 응답 코드: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d("KakaoSignup", "서버 응답 바디: $body")

                    // 서버에서 발급한 JWT(accessToken, refreshToken) 저장
                    val prefs = MyApplication.appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("access_token", body.result.accessToken)
                        putString("refresh_token", body.result.refreshToken)
                        apply()
                    }

                    _kakaoSignupResult.value = Result.success(body)
                } else {
                    val errorBodyStr = response.errorBody()?.string()
                    Log.e("KakaoSignup", "회원가입 실패: $errorBodyStr")
                    _kakaoSignupResult.value = Result.failure(Exception(errorBodyStr ?: "알 수 없는 오류"))
                }
            } catch (e: Exception) {
                Log.e("KakaoSignup", "네트워크 오류 발생", e)
                _kakaoSignupResult.value = Result.failure(e)
            }
        }
    }

}