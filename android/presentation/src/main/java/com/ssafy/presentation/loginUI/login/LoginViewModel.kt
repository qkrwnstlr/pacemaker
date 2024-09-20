package com.ssafy.presentation.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.usecase.user.GetUserInfoUseCase
import com.ssafy.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _signUpEvent: MutableSharedFlow<ResponseResult<Unit>> = MutableSharedFlow()
    val signUpEvent: SharedFlow<ResponseResult<Unit>> = _signUpEvent.asSharedFlow()

    fun checkUser(uid: String, moveToHome: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getUserInfoUseCase(uid) }
            .onSuccess { result ->
                if (result !is ResponseResult.Success) return@onSuccess
                withContext(Dispatchers.Main) { moveToHome() }
            }
    }

    fun signUp(uid: String, name: String?) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { signUpUseCase(uid, name) }
            .onSuccess { result -> makeSuccessEvent(result) }
            .onFailure { error -> makeErrorEvent(error.message ?: ERROR) }
    }

    private suspend fun makeErrorEvent(message: String) {
        val error = ResponseResult.Error<Unit>(message = message)
        _signUpEvent.emit(error)
    }

    private suspend fun makeSuccessEvent(result: ResponseResult<Unit>) {
        _signUpEvent.emit(result)
    }

    companion object {
        const val ERROR = "ERROR!"
    }

}
