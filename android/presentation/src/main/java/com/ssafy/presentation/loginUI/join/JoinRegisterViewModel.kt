package com.ssafy.presentation.loginUI.join

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JoinRegisterViewModel : ViewModel() {
    private val _orderState: MutableStateFlow<Int> = MutableStateFlow(1)
    val orderState: StateFlow<Int> = _orderState.asStateFlow()

    private val _genderState: MutableStateFlow<String> = MutableStateFlow("")
    val genderState: StateFlow<String> = _genderState.asStateFlow()

    fun nextOrder() {
        val prev = _orderState.value

        if (prev > 10) _orderState.value -= 1
        else _orderState.value += 1
    }

    fun resetOrder() {
        _orderState.value = 1
    }

    fun setGender(gender: String) {
        _genderState.value = gender
    }

}
