package com.example.miruni.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStop(stop: Stop)

    @Query("SELECT StopepedAt FROM stopTable WHERE taskId = :taskId")
    fun stoppedTimeByTaskId(taskId: Int): String
}