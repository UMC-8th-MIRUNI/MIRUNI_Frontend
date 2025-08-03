package com.example.miruni

import android.content.Context
import android.util.Log


class HomepageService(private val context: Context) {

    suspend fun getHomepageData(): HomepageResponse?{

        //토큰 임시 저장
        val t = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaHprdGxkaEBnbWFpbC5jb20iLCJleHAiOjE3NTM5NTAzMzMsImlhdCI6MTc1Mzk0NjczM30.6O8qMjpDsn-Ibrc2B9vsg3wciPMKvaWiLqHhUFl641I"
        TokenManager.saveToken(context,t)

        val token = TokenManager.getToken(context)

        if(token == null){
            Log.e("HomepageService", "token을 받아오지 못함")
            return null
        }else{
            Log.d("HomepageService", "token 받아옴")
        }
        return try{
            val bearerToken = "Bearer $token"
            RetrofitInstance.authService.getHomepage(bearerToken)
        }catch (e: Exception){
            Log.e("HomepageService", "홈페이지 전체 정보 조회 api 연동 실패: ${e.message}")
            null
        }
    }

}