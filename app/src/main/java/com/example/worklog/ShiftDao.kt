package com.example.worklog

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShiftDao {

    @Insert
    suspend fun insert(shift: ShiftEntity)

    @Update
    suspend fun update(shift: ShiftEntity)

    @Delete
    suspend fun delete(shift: ShiftEntity)

    @Query(
        "SELECT * FROM shifts " +
                "WHERE month = :month AND year = :year " +
                "ORDER BY date"
    )
    suspend fun getShiftsForMonth(
        month: Int,
        year: Int
    ): List<ShiftEntity>

    @Query(
        "SELECT * FROM shifts " +
                "WHERE id = :id LIMIT 1"
    )
    suspend fun getShiftById(
        id: Int
    ): ShiftEntity?
}