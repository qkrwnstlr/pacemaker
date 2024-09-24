package com.ssafy.presentation.myPageUI.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.response.ResponseResult
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

    fun setNewProfile(initView: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { user = dataStoreRepository.getUser() }
            .onSuccess {
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
        popBack: () -> Unit,
        failToSetProfile: (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val user = user

        runCatching { modifyUserUseCase(uid, user) }
            .onSuccess { response -> checkResponse(response, popBack, failToSetProfile) }
            .onFailure { failToSetProfile(ERROR) }
    }

    private suspend fun checkResponse(
        responseResult: ResponseResult<User>,
        popBack: () -> Unit,
        failToSetProfile: (String) -> Unit
    ) = withContext(Dispatchers.Main) {
        when (responseResult) {

            is ResponseResult.Error -> {
                failToSetProfile(responseResult.message)
            }

            is ResponseResult.Success -> {
                responseResult.data?.let {
                    dataStoreRepository.saveUser(it)
                    popBack()
                } ?: failToSetProfile(ERROR)
            }
        }
    }
}
