package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterPlanViewModel : ViewModel() {
    private val _orderState: MutableStateFlow<Int> = MutableStateFlow(1)
    val orderState: StateFlow<Int> = _orderState.asStateFlow()

    fun nextOrder() {
        val prev = _orderState.value

        if (prev > 10) _orderState.value -= 1
        else _orderState.value += 1
    }

    fun resetOrder() {
        _orderState.value = 1
    }
}