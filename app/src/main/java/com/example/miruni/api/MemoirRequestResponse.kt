package com.example.miruni.api

import com.example.miruni.data.Mood
import com.example.miruni.data.Review

/** 단일 회고 상세 조회 **/
data class MemoirDetailResponse(
    val errorCode: String?,
    val message: String,
    val result: Review
)
data class MemoirDetailRequest(
    val reviewId: Int
)

/** 회고 작성 후 저장 **/
data class Memoir(
    val id: Int,
    val aiPlanId: Int?,
    val planId: Int,
    val mood: Mood,
    val title: String,
    val description: String,
    val achievement: Int,
    val memo: String,
    val createdAt: String
)
data class MemoirSaveResponse(
    val errorCode: String?,
    val message: String,
    val result: Review?
)
data class MemoirSaveRequest(
    val aiPlanId: Int?,
    val planId: Int,
    val mood: Mood,
    val achievement: Int,
    val memo: String
)

/** 회고 수정 **/
data class MemoirUpdateRequst(
    val mood: Mood,
    val achievement: Int,
    val memo: String
)
data class MemoirUpdateResponse(
    val errorCode: String?,
    val message: String,
    val result: Review
)
/*data class  Review (
    val id: Int,
    val aiPlanId: Int,
    val planId: Int,
    val mood: Mood,
    val title: String,
    val achievement: Int,
    val memo: String,
    val createdAt: String
)*/

/** 특정 날짜 회고 목록 조회 **/
data class MemoirDateListResponse(
    val errorCode: String?,
    val message: String,
    val result: List<ReviewByDate>
)
data class ReviewByDate(
    val id: Int,
    val title: String,
    val description: String,
    val createdAt: String
)

/** 날짜 별 회고록 갯수 조회 **/
data class MemoirCountResponse(
    val errorCode: String?,
    val messafe: String,
    val result: List<ReviewDate>?
)
data class ReviewDate(
    val date: String,
    val count: Int
)


/** 회고 날짜 검색 조회 **/
data class MemoirSearchResponse(
    val errorCode: String?,
    val message: String,
    val result: ReviewDate
)