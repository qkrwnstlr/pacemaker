package com.ssafy.presentation.component

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.presentation.databinding.CalendarDayMonthMiniBinding
import java.time.LocalDate

class MiniDateContainer(view: View, dateClicked: (LocalDate) -> (Unit)) : ViewContainer(view) {
    lateinit var day: CalendarDay
    val binding = CalendarDayMonthMiniBinding.bind(view)

    init {
        view.setOnClickListener {
            if (day.position == DayPosition.MonthDate) {
                dateClicked(day.date)
            }
        }
    }
}