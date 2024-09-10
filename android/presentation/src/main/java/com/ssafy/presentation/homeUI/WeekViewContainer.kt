package com.ssafy.presentation.homeUI

import android.view.View
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.presentation.databinding.CalendarDayMonthBinding

class WeekDayViewContainer(view: View) : ViewContainer(view) {
  lateinit var day: WeekDay
  val textView = CalendarDayMonthBinding.bind(view).exOneDayText
  val ly = CalendarDayMonthBinding.bind(view).lyDay

  fun setOnClickListener(onClickListener: (day: WeekDay) -> Unit) {
    view.setOnClickListener { onClickListener(day) }
  }
}