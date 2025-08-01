package com.example.miruni.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    /**
     * 단일 조회
     */
    @Query("SELECT id, scheduleId, title, startTime, endTime, status FROM TaskTable WHERE id = :id")
    fun getTask(id: Int): Task

    /**
     * 테이블 전체 조회
     */
    @Query("SELECT id, scheduleId, title, startTime, endTime, status FROM TaskTable")
    fun getTasks(): List<Task>

    @Query("SELECT id, scheduleId, title, startTime, endTime, status " +
            "FROM TaskTable " +
            "WHERE scheduleId = :scheduleId")
    fun getTasksByScheduleId(scheduleId: Int): List<Task>

    @Query("SELECT * FROM TaskTable")
    suspend fun getTask(): List<Task>

    @Query("DELETE FROM TaskTable WHERE id = :taskid")
    suspend fun deleteTaskById(taskid: Int)
}