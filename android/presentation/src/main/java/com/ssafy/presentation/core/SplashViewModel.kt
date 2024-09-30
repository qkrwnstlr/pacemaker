package com.ssafy.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.ssafy.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _uidState: MutableStateFlow<String?> = MutableStateFlow(null)
    val uidState: StateFlow<String?> = _uidState.asStateFlow()

    fun login() = viewModelScope.launch(Dispatchers.IO) {
        val uid = Firebase.auth.uid ?: ""
        runCatching { getUserInfoUseCase(uid) }
            .onSuccess { _uidState.emit(uid) }
            .onFailure { _uidState.emit("") }
    }

}