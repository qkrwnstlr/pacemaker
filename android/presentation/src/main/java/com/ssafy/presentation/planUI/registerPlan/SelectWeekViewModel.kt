package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek


class SelectWeekViewModel : ViewModel() {

    private val _weekListState: MutableStateFlow<List<DayOfWeek>> = MutableStateFlow(emptyList())
    val weekListState: StateFlow<List<DayOfWeek>> = _weekListState.asStateFlow()

    fun selectDay(day: DayOfWeek, selected: Boolean, isNotValid: (DayOfWeek) -> Unit) {
        val weekList = weekListState.value.toMutableList()

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

}
