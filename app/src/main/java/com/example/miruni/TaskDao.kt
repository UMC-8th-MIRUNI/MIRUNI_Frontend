package com.example.miruni

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Insert
    suspend fun insertAllTask(taskList: List<Task>)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task_table")
    suspend fun getTask(): List<Task>

    @Query("DELETE FROM task_table WHERE id = :taskid")
    suspend fun deleteTaskById(taskid: Int)
}