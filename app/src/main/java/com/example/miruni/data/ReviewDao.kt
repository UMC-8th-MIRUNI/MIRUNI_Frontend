package com.example.miruni.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReviewDao {
    @Insert
    fun insertReview(review: Review)

    @Delete
    fun deleteReiew(review: Review)

    @Query("SELECT * FROM ReviewTable WHERE id = :reviewId")
    fun findReviewById(reviewId: Int) : Review

    // 회고 날짜 검색 조회
    @Query("SELECT * FROM ReviewTable WHERE createdAt = :date")
    fun findReviewByDate(date: String) :  Review
}
