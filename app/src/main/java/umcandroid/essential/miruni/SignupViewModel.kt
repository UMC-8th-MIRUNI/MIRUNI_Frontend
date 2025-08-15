package umcandroid.essential.miruni

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SignupViewModel : ViewModel() {

    //사용자 입력값 저장
    val name = MutableLiveData<String>()
    val birthday = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    var nickname = MutableLiveData<String>()

//    private val _preferenceList = mutableListOf<String>()

    //서버 응답 결과 처리용
    private val _signupResult = MutableLiveData<Result<SignupResponse>>()
    val signupResult: LiveData<Result<SignupResponse>> = _signupResult

//    fun addPreferences(selectedItems: List<String>) {
//        _preferenceList.addAll(selectedItems)  // 중복 허용
//    }
//
//    fun clearPreferences() {
//        _preferenceList.clear()
//    }
//
//    fun getUserPreferenceString(): String = _preferenceList.joinToString(",")

    //api 요청
    fun signup() {
        val request = SignupRequest(
            name = name.value ?: "",
            birthday = birthday.value ?: "",
            email = email.value ?: "",
            phoneNumber = phoneNumber.value ?: "",
            password = password.value ?: "",
            nickname = nickname.value ?: ""
//            userPreference = getUserPreferenceString()
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
                    // ❗ 서버가 보내준 에러 바디 읽기
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
}
