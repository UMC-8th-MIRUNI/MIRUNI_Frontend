package umcandroid.essential.miruni

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SurveyViewModel : ViewModel() {

    // Fragment별 선택값 누적
    val situations = MutableLiveData<List<String>>(emptyList())
    val level = MutableLiveData<String>()
    val reasons = MutableLiveData<List<String>>(emptyList())

    // 서버 응답
    val surveyResult = MutableLiveData<SurveyResponse>()
    val error = MutableLiveData<String>()

    // 토큰 (로그인 후 세팅)
    val accessToken = MutableLiveData<String>()


    fun submitSurvey(repository: SurveyRepository) {
        val token = accessToken.value
        if (token.isNullOrEmpty()) {
            Log.e("SurveyViewModel", "Access token is null or empty")
            error.value = "토큰이 없습니다."
            return
        }

        val request = SurveyRequest(
            situations = situations.value ?: emptyList(),
            level = level.value ?: "",
            reasons = reasons.value ?: emptyList()
        )

        Log.d("SurveyViewModel", "Submitting survey with request: $request")

        viewModelScope.launch {
            try {
                val response = repository.sendSurvey("Bearer $token", request)
                Log.d("SurveyViewModel", "Retrofit response: $response")

                if (response != null) {
                    surveyResult.value = response
                    Log.d("SurveyViewModel", "surveyResult LiveData updated")
                } else {
                    error.value = "서버 응답 오류"
                    Log.e("SurveyViewModel", "Response body is null")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("SurveyViewModel", "submitSurvey Exception: ${e.message}")
            }
        }
    }
}
