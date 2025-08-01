package umcandroid.essential.miruni

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel : ViewModel() {

    //사용자 입력값 저장
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    _loginResult.postValue(Result.success(response.body()!!))
                } else {
                    _loginResult.postValue(Result.failure(Exception("로그인 실패: ${response.code()}")))
                }
            } catch (e: IOException) {
                _loginResult.postValue(Result.failure(Exception("네트워크 오류 발생")))
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }
}