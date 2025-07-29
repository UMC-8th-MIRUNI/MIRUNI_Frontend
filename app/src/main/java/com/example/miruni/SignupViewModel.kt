package com.example.miruni

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel : ViewModel() {
    var name = ""
    var email = ""
    var password = ""
    var password_check = ""
    var isAgree = false
    var nickname = ""

    val signupResult = MutableLiveData<Boolean>()

//    fun signup() {
//        viewModelScope.launch {
//            try {
//                val request = SignupRequest(name, email, password, password_check, isAgree, nickname)
//                val response = RetrofitClient.apiService.signup(request)
//                signupResult.value = response.isSuccessful
//            } catch (e: Exception) {
//                signupResult.value = false
//            }
//        }
//    }
}