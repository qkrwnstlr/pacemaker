package com.ssafy.presentation.core

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var uid: String = "testtest"

    // TODO 파이어베이스 uid 가져와야함!!
    fun setNewUid(newUid: String) {
        uid = newUid
    }

    fun clearUid() {
        uid = ""
    }

}
