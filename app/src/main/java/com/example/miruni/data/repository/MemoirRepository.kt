package com.example.miruni.data.repository

import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.MemoirCountResponse
import com.example.miruni.api.model.MemoirDateListResponse
import com.example.miruni.api.model.MemoirDeliteResponse
import com.example.miruni.api.model.MemoirDetailResponse
import com.example.miruni.api.model.MemoirSaveRequest
import com.example.miruni.api.model.MemoirSaveResponse
import com.example.miruni.api.model.MemoirSearchResponse
import retrofit2.Response

class MemoirRepository(
    private val api: ApiService = getRetrofit().create(ApiService::class.java)
)  {
    /* 날짜별 회고록 갯수 조회 API */
    suspend fun getmemoirCountByDate(token: String): Response<MemoirCountResponse>{
        val reposonse = api.memoirCountByDate(token)
        return reposonse
    }

    /* 회고 날짜 검색 조회 API */
    suspend fun getMemoirSearch(token: String, date: String): Response<MemoirSearchResponse>{
        val response = api.memoirSearch(token, date)
        return response
    }

    /* 특정 날짜 회고 목록 조회 API */
    suspend fun getMemoirDateList(token: String, date: String): Response<MemoirDateListResponse>{
        val response = api.memoirDateList(token, date)
        return response
    }

    /* 회고 단일 상세 조회 API */
    suspend fun getMemoirDetail(token: String, reviewId: Int): Response<MemoirDetailResponse>{
        val response = api.memoirDetail(token, reviewId)
        return response
    }

    /* 회고 삭제 API */
    suspend fun getMemoirDelete(token: String, reviewId: Int): Response<MemoirDeliteResponse>{
        val response = api.memoirDelete(token, reviewId)
        return response
    }

    /* 회고 작성 후 저장 */
    suspend fun getMemoirSave(token: String, contentType: String, registerReview: MemoirSaveRequest): Response<MemoirSaveResponse>{
        val response = api.memoirSave(token, contentType, registerReview)
        return response
    }
}