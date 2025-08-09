package com.example.miruni.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Schedule::class, Task::class, Review::class], version = 1)
abstract class ScheduleDatabase: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        private var instance: ScheduleDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ScheduleDatabase? {
            if (instance == null) {
                synchronized(ScheduleDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ScheduleDatabase::class.java,
                        "schedules-database"//다른 데이터 베이스랑 이름겹치면 꼬임
                    ).allowMainThreadQueries().build()
                }
            }

            return instance
        }
    }
}