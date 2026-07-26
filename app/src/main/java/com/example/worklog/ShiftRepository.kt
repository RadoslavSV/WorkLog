package com.example.worklog

class ShiftRepository(
    private val shiftDao: ShiftDao
) {

    suspend fun insert(
        shift: ShiftEntity
    ) {
        shiftDao.insert(shift)
    }

    suspend fun update(
        shift: ShiftEntity
    ) {
        shiftDao.update(shift)
    }

    suspend fun delete(
        shift: ShiftEntity
    ) {
        shiftDao.delete(shift)
    }

    suspend fun getShiftsForMonth(
        month: Int,
        year: Int
    ): List<ShiftEntity> {

        return shiftDao.getShiftsForMonth(
            month,
            year
        )
    }

    suspend fun getShiftById(
        id: Int
    ): ShiftEntity? {

        return shiftDao.getShiftById(id)
    }
}