package com.ssafy.presentation.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private suspend fun setProfileUrl(newUrl: String) {
        dataStoreRepository.setImgUrl(newUrl)
    }

    fun signUp(
        uid: String,
        name: String?,
        url: String,
        goConnect: () -> Unit,
        goHome: () -> Unit,
        saveUid: suspend (String) -> Unit
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                setProfileUrl(url)
                signUpUseCase(uid, name)
            }.onSuccess { result ->
                saveUid(uid)
                result.data?.let {
                    dataStoreRepository.saveUser(it.userInfoResponse)

                    withContext(Dispatchers.Main) {
                        if (it.isAlreadyExists) goHome()
                        else goConnect()
                    }
                }
            }
        }
}