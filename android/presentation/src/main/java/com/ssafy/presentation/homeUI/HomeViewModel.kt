package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
  private val _trainingState: MutableStateFlow<Int> = MutableStateFlow(2)
  val trainingState: StateFlow<Int> = _trainingState.asStateFlow()
}