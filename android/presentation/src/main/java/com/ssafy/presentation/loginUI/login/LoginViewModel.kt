package com.ssafy.presentation.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.repository.DataStoreRepository
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
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun setProfileUrl(newUrl: String) {
        viewModelScope.launch {
            dataStoreRepository.setImgUrl(newUrl)
        }
    }

    fun checkUser(
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
                checkUidUseCase(uid)
            }
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
                                //todo : saveUser하기
//                                dataStoreRepository.saveUser(it)
                                withContext(Dispatchers.Main) {
                                    goConnect()
                                }
                            }
                    }
                }
        }
}