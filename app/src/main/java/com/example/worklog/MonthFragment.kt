package com.example.worklog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.worklog.databinding.FragmentMonthBinding
import kotlinx.coroutines.launch

class MonthFragment : Fragment() {

    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ShiftRepository

    private var monthIndex: Int = 0
    private var year: Int = 2026

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        monthIndex =
            arguments?.getInt("month") ?: 0

        year =
            arguments?.getInt("year") ?: 2026

        val monthName = listOf(
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"
        )[monthIndex]

        binding.tvMonthTitle.text =
            "$monthName $year"

        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                requireContext()
            )

        val database =
            DatabaseProvider.getDatabase(requireContext())

        repository =
            ShiftRepository(database.shiftDao())

        loadShifts()

        binding.fabAddShift.setOnClickListener {
            val intent =
                android.content.Intent(
                    requireContext(),
                    NewShiftActivity::class.java
                )

            intent.putExtra(
                "month",
                monthIndex
            )

            intent.putExtra(
                "year",
                year
            )

            startActivity(intent)
        }
    }

    private fun loadShifts() {

        viewLifecycleOwner.lifecycleScope.launch {

            val entities =
                repository.getShiftsForMonth(
                    monthIndex,
                    year
                )

            val shifts =
                entities.map {
                    it.toShift()
                }

            binding.recyclerView.adapter =
                ShiftAdapter(
                    shifts,
                    monthIndex,
                    year,

                    onDeleteClick = { shift ->

                        val entity =
                            entities.first {
                                it.date == shift.date &&
                                        it.start == shift.start &&
                                        it.end == shift.end &&
                                        it.breakMinutes == shift.breakMinutes
                            }

                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Delete Shift")
                            .setMessage("Are you sure you want to delete this shift?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete") { _, _ ->

                                viewLifecycleOwner.lifecycleScope.launch {

                                    repository.delete(entity)

                                    loadShifts()
                                }
                            }
                            .show()
                    },

                    onShiftClick = { shift ->

                        val entity =
                            entities.first {
                                it.date == shift.date &&
                                        it.start == shift.start &&
                                        it.end == shift.end &&
                                        it.breakMinutes == shift.breakMinutes
                            }

                        val intent =
                            android.content.Intent(
                                requireContext(),
                                EditShiftActivity::class.java
                            )

                        intent.putExtra(
                            "shiftId",
                            entity.id
                        )

                        startActivity(intent)
                    }
                )

            updateSummary(shifts)
        }
    }

    private fun updateSummary(
        shifts: List<Shift>
    ) {

        val totalMinutes =
            calculateTotalMinutes(shifts)

        val hours =
            totalMinutes / 60

        val minutes =
            totalMinutes % 60

        binding.tvTotal.text =
            "Total: ${hours}h ${minutes}m"

        val neededMinutes =
            shifts.size * 8 * 60

        val neededHours =
            neededMinutes / 60

        binding.tvNeeded.text =
            "Needed: ${neededHours}h"

        val differenceMinutes =
            totalMinutes - neededMinutes

        val differenceHours =
            kotlin.math.abs(
                differenceMinutes / 60
            )

        val differenceRemainingMinutes =
            kotlin.math.abs(
                differenceMinutes % 60
            )

        val sign =
            if (differenceMinutes >= 0) {
                "+"
            } else {
                "-"
            }

        binding.tvDifference.text =
            "Difference: $sign${differenceHours}h " +
                    "${differenceRemainingMinutes}m"

        if (differenceMinutes >= 0) {
            binding.tvDifference.setTextColor(
                android.graphics.Color.GREEN
            )
        } else {
            binding.tvDifference.setTextColor(
                android.graphics.Color.RED
            )
        }
    }

    private fun calculateTotalMinutes(
        items: List<Shift>
    ): Int {

        return items.sumOf { shift ->

            val start =
                shift.start.split(":")

            val end =
                shift.end.split(":")

            val startMin =
                start[0].toInt() * 60 +
                        start[1].toInt()

            val endMin =
                end[0].toInt() * 60 +
                        end[1].toInt()

            (endMin - startMin) -
                    shift.breakMinutes
        }
    }

    override fun onResume() {
        super.onResume()

        if (_binding != null) {
            loadShifts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {

        fun newInstance(
            month: Int,
            year: Int
        ): MonthFragment {

            val fragment =
                MonthFragment()

            val args =
                Bundle()

            args.putInt(
                "month",
                month
            )

            args.putInt(
                "year",
                year
            )

            fragment.arguments =
                args

            return fragment
        }
    }
}