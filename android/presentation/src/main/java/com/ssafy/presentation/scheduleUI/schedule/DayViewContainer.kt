package com.ssafy.presentation.scheduleUI.schedule

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.presentation.databinding.CalendarDayMonthBinding
import java.time.LocalDate

class DayViewContainer(view: View, dateClicked: (LocalDate) -> (Unit)) : ViewContainer(view) {
    lateinit var day: CalendarDay
    val ly = CalendarDayMonthBinding.bind(view).lyDay
    val textView = CalendarDayMonthBinding.bind(view).exOneDayText
    val exThreeDotView = CalendarDayMonthBinding.bind(view).exThreeDotView

    init {
        view.setOnClickListener {
            if (day.position == DayPosition.MonthDate) {
                dateClicked(day.date)
            }
        }
    }
}