package com.example.worklog

data class Shift(
    val date: Int,
    val start: String,
    val end: String,
    val breakMinutes: Int
)