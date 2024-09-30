package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import com.ssafy.presentation.utils.weekMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek


class SelectWeekViewModel : ViewModel() {

    private val _weekListState: MutableStateFlow<Set<DayOfWeek>> = MutableStateFlow(emptySet())
    val weekListState: StateFlow<Set<DayOfWeek>> = _weekListState.asStateFlow()

    fun selectDay(day: DayOfWeek, selected: Boolean, isNotValid: (DayOfWeek) -> Unit) {
        val weekList = weekListState.value.toMutableSet()

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
        val weekList = weekListState.value.toMutableSet()

        days.forEach { day -> weekMap[day]?.let { weekList.add(it) } }
        _weekListState.update { weekList }
    }

}
