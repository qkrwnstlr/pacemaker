package com.ssafy.presentation.myPageUI.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.User
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.usecase.user.ModifyUserUseCase
import com.ssafy.presentation.myPageUI.data.Profile
import com.ssafy.presentation.myPageUI.data.toUser
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val modifyUserUseCase: ModifyUserUseCase
) : ViewModel() {

    var profile: Profile = Profile()

    fun setNewProfile(newProfile: Profile) {
        profile = newProfile
    }

    private fun setUid(uid: String) {
        profile = profile.copy(uid = uid)
    }

    fun setName(name: String) {
        profile = profile.copy(name = name)
    }

    fun setAge(age: Int) {
        profile = profile.copy(age = age)
    }

    fun setHeight(height: Int) {
        profile = profile.copy(height = height)
    }

    fun setWeight(weight: Int) {
        profile = profile.copy(weight = weight)
    }

    fun setGender(gender: String) {
        profile = profile.copy(gender = gender)
    }

    fun modifyProfile(
        uid: String,
        popBack: () -> Unit,
        failToSetProfile: (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        setUid(uid)
        val user = profile.toUser()

        runCatching { modifyUserUseCase(user) }
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
                responseResult.data?.let { popBack() } ?: failToSetProfile(ERROR)
            }
        }
    }
}
