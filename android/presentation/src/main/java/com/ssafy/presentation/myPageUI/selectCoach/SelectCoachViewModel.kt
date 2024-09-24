package com.ssafy.presentation.myPageUI.selectCoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.usecase.user.SetCoachUseCase
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCoachViewModel @Inject constructor(
    private val setCoachUseCase: SetCoachUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun setCoach(
        uid: String,
        coachIndex: Long,
        moveToNextFragment: suspend () -> Unit,
        failToSetCoach: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            setCoachUseCase(uid, coachIndex)
        }.onSuccess {
            when (it) {
                is ResponseResult.Error -> failToSetCoach(it.message)
                is ResponseResult.Success -> {
                    saveNewInfo(coachIndex)
                    moveToNextFragment()
                }
            }
        }.onFailure {
            failToSetCoach(ERROR)
        }
    }

    private suspend fun saveNewInfo(coachIndex: Long) = runCatching {
        dataStoreRepository.getUser()
    }.onSuccess {
        val newUser = it.copy(coachNumber = coachIndex)
        dataStoreRepository.saveUser(newUser)
    }

    companion object {
        const val MIKE = 1L
        const val JAMIE = 2L
        const val DANNY = 3L
    }
}
