package com.example.worklog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.worklog.databinding.ItemShiftBinding

class ShiftAdapter(
    private val items: List<Shift>,
    private val month: Int,
    private val year: Int,
    private val onDeleteClick: (Shift) -> Unit,
    private val onShiftClick: (Shift) -> Unit
) : RecyclerView.Adapter<ShiftAdapter.VH>() {

    class VH(val binding: ItemShiftBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {

        val binding = ItemShiftBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return VH(binding)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {

        val item = items[position]

        holder.binding.tvId.text =
            (position + 1).toString()

        val calendar = java.util.Calendar.getInstance()

        calendar.set(
            year,
            month,
            item.date
        )

        val dayOfWeek = when (
            calendar.get(java.util.Calendar.DAY_OF_WEEK)
        ) {
            java.util.Calendar.MONDAY -> "MON"
            java.util.Calendar.TUESDAY -> "TUE"
            java.util.Calendar.WEDNESDAY -> "WED"
            java.util.Calendar.THURSDAY -> "THU"
            java.util.Calendar.FRIDAY -> "FRI"
            java.util.Calendar.SATURDAY -> "SAT"
            java.util.Calendar.SUNDAY -> "SUN"
            else -> ""
        }

        holder.binding.tvDate.text =
            String.format(
                java.util.Locale.getDefault(),
                "%02d.%02d%n%s",
                item.date,
                month + 1,
                dayOfWeek
            )

        holder.binding.tvShift.text =
            "${item.start} - ${item.end}"

        val durationMinutes =
            calculateMinutes(
                item.start,
                item.end
            ) - item.breakMinutes

        val hours =
            durationMinutes / 60

        val minutes =
            durationMinutes % 60

        holder.binding.tvHours.text =
            "${hours}h ${minutes}m"

        // Delete button
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }

        // Click anywhere else on the row
        holder.binding.root.setOnClickListener {
            onShiftClick(item)
        }
    }

    override fun getItemCount(): Int =
        items.size

    private fun calculateMinutes(
        start: String,
        end: String
    ): Int {

        val startParts =
            start.split(":")

        val endParts =
            end.split(":")

        val startMinutes =
            startParts[0].toInt() * 60 +
                    startParts[1].toInt()

        val endMinutes =
            endParts[0].toInt() * 60 +
                    endParts[1].toInt()

        return endMinutes - startMinutes
    }
}