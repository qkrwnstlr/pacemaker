package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.user.GetCoachUseCase
import com.ssafy.presentation.planUI.registerPlan.adapter.ChatData
import com.ssafy.presentation.utils.toCoachMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class RegisterPlanViewModel @Inject constructor(
    private val getCoachUseCase: GetCoachUseCase
) : ViewModel() {

    private var coachIndex: Long = 1
    private val _planData = MutableStateFlow<List<ChatData>>(emptyList())
    val planData = _planData.asStateFlow()

    private suspend fun initChatMessage(index: Long, sendAble: (Boolean) -> Unit) {
        val coachMessages = index.toCoachMessage()
            .map { ChatData.CoachData(it, index) }
            .toMutableList()

        flow {
            coachMessages.forEach { message ->
                emit(message)
                delay(SECONDS.seconds)
            }
        }.onCompletion {
            withContext(Dispatchers.Main) {
                sendAble(true)
            }
        }.collect { coachMessage ->
            val newList = _planData.value.toMutableList()
            newList.add(coachMessage)
            _planData.emit(newList)
        }
    }

    fun getCoach(uid: String, sendAble: (Boolean) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getCoachUseCase(uid) }
            .onSuccess {
                it.data?.coachNumber?.let { number ->
                    coachIndex = number
                    initChatMessage(number, sendAble)
                }
            }
    }

    // TODO 서버로 데이터 전송하는 것도 해야함!
    fun sendMyMessage(text: String) = viewModelScope.launch(Dispatchers.IO) {
        val newList = _planData.value.toMutableList()
        val myMessage = ChatData.MyData(text)
        val coachMessage = ChatData.CoachData(COACH_CHATTING, coachIndex)
        newList.add(myMessage)
        newList.add(coachMessage)
        _planData.emit(newList)
    }

    companion object {
        const val COACH_CHATTING = "AI 코치가 답변 중 입니다"
        const val SECONDS = 2
    }
}