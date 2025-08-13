package com.example.miruni.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlarmDao {

    @Insert
    suspend fun insertAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarmTable")
    suspend fun getAllAlarm(): List<Alarm>

}