package com.ssafy.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.ssafy.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    var uid: String? = null

    fun login() = viewModelScope.launch(Dispatchers.IO) {
        val uid = Firebase.auth.uid ?: return@launch
        runCatching { getUserInfoUseCase(uid) }
            .onSuccess { this@SplashViewModel.uid = uid }
            .onFailure { it.printStackTrace() }
    }

}