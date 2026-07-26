package com.example.worklog

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.worklog.databinding.ActivityNewShiftBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditShiftActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewShiftBinding

    private val shiftCalendar = Calendar.getInstance()

    private var shiftId: Int = -1

    private lateinit var repository: ShiftRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewShiftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shiftId = intent.getIntExtra(
            "shiftId",
            -1
        )

        if (shiftId == -1) {
            finish()
            return
        }

        val database =
            DatabaseProvider.getDatabase(this)

        repository =
            ShiftRepository(
                database.shiftDao()
            )

        lifecycleScope.launch {

            val shift =
                repository.getShiftById(shiftId)

            if (shift == null) {
                finish()
                return@launch
            }

            loadShift(shift)
        }

        binding.btnShiftDate.setOnClickListener {
            showShiftDatePicker()
        }

        binding.etStartHour.setOnFocusChangeListener { _, _ ->
            calculateShiftTotal()
        }

        binding.etStartMinute.setOnFocusChangeListener { _, _ ->
            calculateShiftTotal()
        }

        binding.etEndHour.setOnFocusChangeListener { _, _ ->
            calculateShiftTotal()
        }

        binding.etEndMinute.setOnFocusChangeListener { _, _ ->
            calculateShiftTotal()
        }

        binding.etBreak.setOnFocusChangeListener { _, _ ->
            calculateShiftTotal()
        }

        binding.btnSaveShift.setOnClickListener {
            updateShift()
        }
    }

    private fun loadShift(
        shift: ShiftEntity
    ) {

        shiftCalendar.set(
            Calendar.YEAR,
            shift.year
        )

        shiftCalendar.set(
            Calendar.MONTH,
            shift.month
        )

        shiftCalendar.set(
            Calendar.DAY_OF_MONTH,
            shift.date
        )

        updateShiftDateButton()

        val startParts =
            shift.start.split(":")

        val endParts =
            shift.end.split(":")

        binding.etStartHour.setText(
            startParts[0]
        )

        binding.etStartMinute.setText(
            startParts[1]
        )

        binding.etEndHour.setText(
            endParts[0]
        )

        binding.etEndMinute.setText(
            endParts[1]
        )

        binding.etBreak.setText(
            shift.breakMinutes.toString()
        )

        calculateShiftTotal()
    }

    private fun updateShiftDateButton() {

        val formatter =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        binding.btnShiftDate.text =
            formatter.format(
                shiftCalendar.time
            )
    }

    private fun showShiftDatePicker() {

        val datePicker =
            DatePickerDialog(
                this,
                { _, selectedYear,
                  selectedMonth,
                  selectedDay ->

                    shiftCalendar.set(
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    )

                    updateShiftDateButton()
                },
                shiftCalendar.get(
                    Calendar.YEAR
                ),
                shiftCalendar.get(
                    Calendar.MONTH
                ),
                shiftCalendar.get(
                    Calendar.DAY_OF_MONTH
                )
            )

        datePicker.show()
    }

    private fun calculateShiftTotal() {

        val startHour =
            binding.etStartHour.text
                .toString()
                .toIntOrNull()

        val startMinute =
            binding.etStartMinute.text
                .toString()
                .toIntOrNull()

        val endHour =
            binding.etEndHour.text
                .toString()
                .toIntOrNull()

        val endMinute =
            binding.etEndMinute.text
                .toString()
                .toIntOrNull()

        val breakMinutes =
            binding.etBreak.text
                .toString()
                .toIntOrNull()
                ?: 0

        if (
            startHour == null ||
            startMinute == null ||
            endHour == null ||
            endMinute == null
        ) {
            binding.tvShiftTotal.text =
                "Total: 0h 0m"

            return
        }

        if (
            startHour !in 0..23 ||
            endHour !in 0..23 ||
            startMinute !in 0..59 ||
            endMinute !in 0..59
        ) {
            binding.tvShiftTotal.text =
                "Total: Invalid"

            return
        }

        val startMinutes =
            startHour * 60 +
                    startMinute

        val endMinutes =
            endHour * 60 +
                    endMinute

        val totalMinutes =
            (endMinutes - startMinutes) -
                    breakMinutes

        if (totalMinutes < 0) {
            binding.tvShiftTotal.text =
                "Total: Invalid"

            return
        }

        val hours =
            totalMinutes / 60

        val minutes =
            totalMinutes % 60

        binding.tvShiftTotal.text =
            "Total: ${hours}h ${minutes}m"
    }

    private fun updateShift() {

        val startHour =
            binding.etStartHour.text
                .toString()
                .toIntOrNull()

        val startMinute =
            binding.etStartMinute.text
                .toString()
                .toIntOrNull()

        val endHour =
            binding.etEndHour.text
                .toString()
                .toIntOrNull()

        val endMinute =
            binding.etEndMinute.text
                .toString()
                .toIntOrNull()

        val breakMinutes =
            binding.etBreak.text
                .toString()
                .toIntOrNull()
                ?: 0

        if (
            startHour == null ||
            startMinute == null ||
            endHour == null ||
            endMinute == null
        ) {
            return
        }

        if (
            startHour !in 0..23 ||
            endHour !in 0..23 ||
            startMinute !in 0..59 ||
            endMinute !in 0..59
        ) {
            return
        }

        val startTime =
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                startHour,
                startMinute
            )

        val endTime =
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                endHour,
                endMinute
            )

        val updatedShift =
            ShiftEntity(
                id = shiftId,
                date =
                    shiftCalendar.get(
                        Calendar.DAY_OF_MONTH
                    ),
                month =
                    shiftCalendar.get(
                        Calendar.MONTH
                    ),
                year =
                    shiftCalendar.get(
                        Calendar.YEAR
                    ),
                start = startTime,
                end = endTime,
                breakMinutes = breakMinutes
            )

        lifecycleScope.launch {

            repository.update(
                updatedShift
            )

            finish()
        }
    }
}