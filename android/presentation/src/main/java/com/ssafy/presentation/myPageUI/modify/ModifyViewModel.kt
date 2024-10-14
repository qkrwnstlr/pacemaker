package com.ssafy.presentation.myPageUI.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.user.ModifyUserUseCase
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val modifyUserUseCase: ModifyUserUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var user: User = User()

    fun setProfile(initView: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { dataStoreRepository.getUser() }
            .onSuccess {
                user = it
                withContext(Dispatchers.Main) {
                    initView()
                }
            }
    }

    fun setName(name: String) {
        user = user.copy(name = name)
    }

    fun setAge(age: Int) {
        user = user.copy(age = age)
    }

    fun setHeight(height: Int) {
        user = user.copy(height = height)
    }

    fun setWeight(weight: Int) {
        user = user.copy(weight = weight)
    }

    fun setGender(gender: String) {
        user = user.copy(gender = gender)
    }

    fun modifyProfile(
        uid: String,
        popBack: suspend () -> Unit,
        failToSetProfile: (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { modifyUserUseCase(uid, user) }
            .onSuccess { popBack() }
            .onFailure { failToSetProfile(it.message ?: ERROR) }
    }
}
