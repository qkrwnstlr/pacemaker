package com.ssafy.presentation.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _uid: MutableStateFlow<String> = MutableStateFlow("")
    var uid: StateFlow<String> = _uid.asStateFlow()

    suspend fun setNewUid(newUid: String) {
        _uid.emit(newUid)
    }

    suspend fun clearUid() {
        _uid.emit("")
    }

}
