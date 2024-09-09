package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
  private val _trainingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val trainingState: StateFlow<Boolean> = _trainingState.asStateFlow()
}