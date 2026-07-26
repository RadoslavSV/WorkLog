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

class NewShiftActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewShiftBinding

    private val shiftCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewShiftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val month = intent.getIntExtra("month", 0)
        val year = intent.getIntExtra("year", 2026)

        // Set the calendar to the selected month and year
        shiftCalendar.set(
            Calendar.YEAR,
            year
        )

        shiftCalendar.set(
            Calendar.MONTH,
            month
        )

        updateShiftDateButton()

        // Default start time = current time
        val currentTime = Calendar.getInstance()

        binding.etStartHour.setText(
            String.format(
                Locale.getDefault(),
                "%02d",
                currentTime.get(Calendar.HOUR_OF_DAY)
            )
        )

        binding.etStartMinute.setText(
            String.format(
                Locale.getDefault(),
                "%02d",
                currentTime.get(Calendar.MINUTE)
            )
        )

        // Default end time = 16:00
        binding.etEndHour.setText("16")
        binding.etEndMinute.setText("00")

        // Date picker
        binding.btnShiftDate.setOnClickListener {
            showShiftDatePicker()
        }

        // Recalculate total when fields lose focus
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

        calculateShiftTotal()

        // Save shift
        binding.btnSaveShift.setOnClickListener {
            saveShift()
        }
    }

    private fun updateShiftDateButton() {

        val formatter = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        binding.btnShiftDate.text =
            formatter.format(shiftCalendar.time)
    }

    private fun showShiftDatePicker() {

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                shiftCalendar.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )

                updateShiftDateButton()
            },
            shiftCalendar.get(Calendar.YEAR),
            shiftCalendar.get(Calendar.MONTH),
            shiftCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun calculateShiftTotal() {

        val startHour =
            binding.etStartHour.text.toString().toIntOrNull()

        val startMinute =
            binding.etStartMinute.text.toString().toIntOrNull()

        val endHour =
            binding.etEndHour.text.toString().toIntOrNull()

        val endMinute =
            binding.etEndMinute.text.toString().toIntOrNull()

        val breakMinutes =
            binding.etBreak.text.toString().toIntOrNull() ?: 0

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
            startHour * 60 + startMinute

        val endMinutes =
            endHour * 60 + endMinute

        val totalMinutes =
            (endMinutes - startMinutes) - breakMinutes

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

    private fun saveShift() {

        val startHour =
            binding.etStartHour.text.toString().toIntOrNull()

        val startMinute =
            binding.etStartMinute.text.toString().toIntOrNull()

        val endHour =
            binding.etEndHour.text.toString().toIntOrNull()

        val endMinute =
            binding.etEndMinute.text.toString().toIntOrNull()

        val breakMinutes =
            binding.etBreak.text.toString().toIntOrNull() ?: 0

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

        val month =
            shiftCalendar.get(Calendar.MONTH)

        val year =
            shiftCalendar.get(Calendar.YEAR)

        val date =
            shiftCalendar.get(Calendar.DAY_OF_MONTH)

        val shift =
            ShiftEntity(
                date = date,
                month = month,
                year = year,
                start = startTime,
                end = endTime,
                breakMinutes = breakMinutes
            )

        val database =
            DatabaseProvider.getDatabase(this)

        val repository =
            ShiftRepository(
                database.shiftDao()
            )

        lifecycleScope.launch {

            repository.insert(shift)

            finish()
        }
    }
}