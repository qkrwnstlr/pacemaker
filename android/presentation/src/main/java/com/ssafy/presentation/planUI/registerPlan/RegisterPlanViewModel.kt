package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.user.GetCoachUseCase
import com.ssafy.presentation.planUI.registerPlan.adapter.ChatData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterPlanViewModel @Inject constructor(
    private val getCoachUseCase: GetCoachUseCase
) : ViewModel() {

    private var coachIndex: Long? = null
    private val _planData = MutableStateFlow<List<ChatData>>(emptyList())
    val planData = _planData.asStateFlow()

    fun getCoach(uid: String) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getCoachUseCase(uid) }
            .onSuccess { it.data?.coachNumber?.let { number -> coachIndex = number } }
    }

    // TODO 서버로 데이터 전송하는 것도 해야함!
    fun sendMyMessage(text: String) = viewModelScope.launch(Dispatchers.IO) {
        val myMessage = ChatData.MyData(text)
        val newList = _planData.value.toMutableList()
        newList.add(myMessage)
        _planData.emit(newList)
    }
}