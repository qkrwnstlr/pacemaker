package com.ssafy.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uid: MutableStateFlow<String> = MutableStateFlow("")
    var uid: StateFlow<String> = _uid.asStateFlow()

    suspend fun setNewUid(newUid: String) {
        _uid.emit(newUid)
    }

    fun updateNewUid(newUid: String) {
        _uid.update { newUid }
    }

    fun clearUid(goLogin: () -> Unit) = viewModelScope.launch {
        runCatching { _uid.emit("") }
            .onSuccess { goLogin() }
    }

}
