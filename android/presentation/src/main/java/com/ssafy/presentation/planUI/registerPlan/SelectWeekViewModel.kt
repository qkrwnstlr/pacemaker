package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import com.ssafy.presentation.utils.weekMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.util.SortedSet
import java.util.TreeSet


class SelectWeekViewModel : ViewModel() {

    private val _weekListState: MutableStateFlow<SortedSet<DayOfWeek>> = MutableStateFlow(TreeSet())
    val weekListState: StateFlow<SortedSet<DayOfWeek>> = _weekListState.asStateFlow()

    fun selectDay(day: DayOfWeek, selected: Boolean, isNotValid: (DayOfWeek) -> Unit) {
        val weekList = TreeSet(weekListState.value)

        if (selected) {
            val prevDay = day.minus(1)
            val nextDay = day.plus(1)

            if (weekList.contains(prevDay) || weekList.contains(nextDay)) isNotValid(day)
            else weekList.add(day)

        } else {
            weekList.remove(day)
        }

        _weekListState.update { weekList }
    }

    fun addDays(days: List<String>) {
        val weekList = TreeSet(weekListState.value)

        days.forEach { day -> weekMap[day]?.let { weekList.add(it) } }
        _weekListState.update { weekList }
    }

}
