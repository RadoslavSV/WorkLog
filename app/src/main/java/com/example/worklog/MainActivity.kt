package com.example.worklog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.worklog.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startYear = 2026
        val startMonth = Calendar.JANUARY
        val startPosition = 1200

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int = Int.MAX_VALUE

            override fun createFragment(position: Int): Fragment {
                val monthOffset = position - startPosition

                val calendar = Calendar.getInstance()
                calendar.set(startYear, startMonth, 1)
                calendar.add(Calendar.MONTH, monthOffset)

                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)

                return MonthFragment.newInstance(month, year)
            }
        }

        val currentCalendar = Calendar.getInstance()

        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)

        val monthOffset =
            (currentYear - startYear) * 12 +
                    (currentMonth - startMonth)

        binding.viewPager.setCurrentItem(
            startPosition + monthOffset,
            false
        )
    }
}