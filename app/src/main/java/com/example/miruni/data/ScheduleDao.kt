package com.example.miruni.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScheduleDao {

    @Insert
    fun insert(schedule: Schedule)

    @Update
    fun update(schedule: Schedule)

    @Delete
    fun delete(schedule: Schedule)

    /**
     * 단일 조회
     */
    @Query("SELECT id, title, comment, date, priority FROM ScheduleTable WHERE id = :id")
    fun getSchedule(id: Int): Schedule

    /**
     * 테이블 전체 조회
     */
    @Query("SELECT id, title, comment, date, priority FROM ScheduleTable")
    fun getSchedules(): List<Schedule>

    /**
     * 날짜별 Schedule 정리
     */
    @Query("SELECT id, title, comment, date, priority FROM ScheduleTable WHERE date = :date")
    fun getScheduleByDate(date: String): List<Schedule>
}