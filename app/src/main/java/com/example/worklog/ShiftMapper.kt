package com.example.worklog

fun ShiftEntity.toShift(): Shift {
    return Shift(
        date = date,
        start = start,
        end = end,
        breakMinutes = breakMinutes
    )
}