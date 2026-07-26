package com.example.worklog

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShiftEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shiftDao(): ShiftDao
}