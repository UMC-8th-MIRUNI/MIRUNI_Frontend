package com.example.miruni.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlanDao {
    @Insert
    fun insert(plan: Plan)

    @Update
    fun update(plan: Plan)

    @Delete
    fun delete(plan: Plan)

    /**
     * 단일 조회
     */
    @Query("SELECT * FROM PlanTable WHERE id = :id")
    fun getPlan(id: Int): Plan
}