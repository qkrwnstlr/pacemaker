package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.Context
import com.ssafy.domain.dto.plan.Plan
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.usecase.plan.ChatForPlanUseCase
import com.ssafy.presentation.planUI.registerPlan.adapter.ChatData
import com.ssafy.presentation.utils.toCoachMessage
import com.ssafy.presentation.utils.toUserInfo
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
    private val dataStoreRepository: DataStoreRepository,
    private val chatForPlanUseCase: ChatForPlanUseCase
) : ViewModel() {

    private var coachIndex: Long = 1

    private val _chatData = MutableStateFlow<List<ChatData>>(emptyList())
    private val _contextData = MutableStateFlow(Context())
    private val _planData = MutableStateFlow(Plan())

    val chatData = _chatData.asStateFlow()
    val contextData = _contextData.asStateFlow()
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
            val newList = _chatData.value.toMutableList()
            newList.add(coachMessage)
            _chatData.emit(newList)
        }
    }

    fun getCoach(sendAble: (Boolean) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { dataStoreRepository.getUser() }
            .onSuccess {
                coachIndex = it.coachNumber
                initChatMessage(it.coachNumber, sendAble)
                val context = contextData.value.copy(userInfo = it.toUserInfo())
                _contextData.emit(context)
            }
    }

    fun sendMyMessage(message: String, failToMakeChat: suspend (String) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val newList = _chatData.value.toMutableList()
            val myMessage = ChatData.MyData(message)
            val coachMessage = ChatData.CoachData(COACH_CHATTING, coachIndex)
            newList.add(myMessage)
            newList.add(coachMessage)
            _chatData.emit(newList)
            makeChat(message, failToMakeChat)
        }

    private fun makeChat(text: String, failToMakeChat: suspend (String) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val chat = Chat(message = text, context = contextData.value, plan = planData.value)
            runCatching { chatForPlanUseCase(chat) }
                .onSuccess { response -> checkResponse(response, failToMakeChat) }
                .onFailure {
                    it.printStackTrace()
                    failToMakeChat(FAILURE)
                }
        }

    private suspend fun checkResponse(
        responseResult: ResponseResult<Chat>,
        failToMakeChat: suspend (String) -> Unit
    ) = when (responseResult) {

        is ResponseResult.Success -> {
            responseResult.data?.let { emitChatResponse(it) } ?: failToMakeChat(FAILURE)
        }

        is ResponseResult.Error -> {
            failToMakeChat(responseResult.message)
        }
    }

    private suspend fun emitChatResponse(chat: Chat) {
        val message = chat.message
        val coachChat = ChatData.CoachData(message, coachIndex)
        val newList = chatData.value.toMutableList()
        newList.removeLastOrNull()
        newList.add(coachChat)

        _chatData.emit(newList)
        _planData.emit(chat.plan)
        _contextData.emit(chat.context)
    }

    companion object {
        const val COACH_CHATTING = "AI 코치가 답변 중 입니다"
        const val SECONDS = 2
        const val FAILURE = "오류가 발생하였습니다. 잠시 후에 시도해주세요."
    }
}