package umcandroid.essential.miruni

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Response

class LogoutViewModel : ViewModel() {

    private val _logoutResult = MutableLiveData<Boolean>()
    val logoutResult: LiveData<Boolean> get() = _logoutResult

    fun logout(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authService.logout("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.message == "OK") {
                        _logoutResult.value = true
                    } else {
                        _logoutResult.value = false
                    }
                } else {
                    _logoutResult.value = false
                }
            } catch (e: Exception) {
                _logoutResult.value = false
            }
        }
    }

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> get() = _deleteResult

    fun deleteAccount(token: String?) {
        if (token.isNullOrEmpty()) {
            _deleteResult.value = false
            return
        }

        viewModelScope.launch {
            try {
                val response: Response<DeleteAccountResponse> =
                    RetrofitInstance.authService.deleteAccount("Bearer $token")
                _deleteResult.value = response.isSuccessful && response.body()?.message == "OK"
            } catch (e: Exception) {
                _deleteResult.value = false
            }
        }
    }
}