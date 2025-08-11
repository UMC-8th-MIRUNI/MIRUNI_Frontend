package com.example.miruni.ui.homepage

import android.content.Context
import android.util.Log
import com.example.miruni.RetrofitInstance
import com.example.miruni.TokenManager
import com.example.miruni.api.HomepageResponse

const val t = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwiZXhwIjoxNzU0ODE0OTc0LCJ0eXBlIjoiQUNDRVNTIiwiaWF0IjoxNzU0ODExMzc0fQ.fXrlQk2FzoTCtebm1PFNBRvqXVTZQT5M3rB-uprxSUc"

class HomepageService(private val context: Context) {
    suspend fun getHomepageData(): HomepageResponse?{

        //토큰 임시 저장
        TokenManager.saveToken(context, t)

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