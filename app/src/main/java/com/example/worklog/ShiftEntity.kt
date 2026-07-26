package com.example.worklog

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class ShiftEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: Int,

    val month: Int,

    val year: Int,

    val start: String,

    val end: String,

    val breakMinutes: Int
)