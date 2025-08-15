package com.example.miruni

class SurveyRepository(private val api: AuthService) {

    suspend fun sendSurvey(token: String, request: SurveyRequest): SurveyResponse? {
        val response = api.sendSurvey(token, request)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}