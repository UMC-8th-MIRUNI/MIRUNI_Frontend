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
    @Query("SELECT id, scheduleId, title, executeDay, startTime, endTime, status FROM TaskTable WHERE id = :id")
    fun getTask(id: Int): Task

    /**
     * 테이블 전체 조회
     */
    @Query("SELECT id, scheduleId, title, executeDay, startTime, endTime, status FROM TaskTable")
    fun getTasks(): List<Task>

    /**
     * Schedule Id로 Schedule을 나눈 Task 모두 조회
     */
    @Query("SELECT id, scheduleId, title, executeDay, startTime, endTime, status " +
            "FROM TaskTable " +
            "WHERE scheduleId = :scheduleId")
    fun getTasksByScheduleId(scheduleId: Int): List<Task>

    /**
     * Task 수행 날짜로 Task 조회
     */
    @Query("SELECT * FROM TaskTable WHERE executeDay = :executeDay")
    fun getTasksByDay(executeDay: String): List<Task>

    @Query("SELECT * FROM TaskTable")
    suspend fun getTask(): List<Task>

    // id통해서 task 삭제
    @Query("DELETE FROM TaskTable WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
    // id통해서 title 반환
    @Query("SELECT title FROM TaskTable WHERE id = :taskId")
    suspend fun getTitleFromId(taskId: Int) : String

}
