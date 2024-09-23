package com.ssafy.presentation.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.user.CheckUidUseCase
import com.ssafy.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val checkUidUseCase: CheckUidUseCase,
) : ViewModel() {

    fun checkUser(
        uid: String,
        name: String?,
        goConnect: () -> Unit,
        goHome: () -> Unit,
        saveUid: suspend (String) -> Unit
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { checkUidUseCase(uid) }
                .onSuccess { result ->
                    if (result.data?.isAlreadyExists == true) {
                        saveUid(uid)
                        withContext(Dispatchers.Main) {
                            goHome()
                        }
                    } else {
                        runCatching { signUpUseCase(uid, name) }
                            .onSuccess {
                                saveUid(uid)
                                withContext(Dispatchers.Main) {
                                    goConnect()
                                }
                            }
                    }
                }
        }
}