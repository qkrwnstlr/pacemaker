package com.ssafy.presentation.myPageUI.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.response.ResponseResult
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _userInfo: MutableStateFlow<User> = MutableStateFlow(User())
    val userInfo: StateFlow<User> = _userInfo.asStateFlow()

    fun getUserInfo(
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { _userInfo.emit(dataStoreRepository.getUser()) }
    }

    private suspend fun checkResponse(
        responseResult: ResponseResult<User>,
        failToGetInfo: (String) -> Unit
    ) = withContext(Dispatchers.Main) {
        when (responseResult) {

            is ResponseResult.Error -> {
                failToGetInfo(responseResult.message)
            }

            is ResponseResult.Success -> {
                responseResult.data?.let { _userInfo.emit(it) } ?: failToGetInfo(ERROR)
            }
        }
    }

}
