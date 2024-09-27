package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.Context
import com.ssafy.domain.dto.plan.Plan
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.plan.UserInfo
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.plan.ChatForPlanUseCase
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.domain.usecase.plan.MakePlanUseCase
import com.ssafy.domain.utils.ifNotHuman
import com.ssafy.domain.utils.ifZero
import com.ssafy.presentation.planUI.registerPlan.adapter.ChatData
import com.ssafy.presentation.utils.ERROR
import com.ssafy.presentation.utils.toCoachMessage
import com.ssafy.presentation.utils.toPlan
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
    private val chatForPlanUseCase: ChatForPlanUseCase,
    private val makePlanUseCase: MakePlanUseCase,
    private val getPlanInfoUseCase: GetPlanInfoUseCase
) : ViewModel() {

    private var coachIndex: Long = 1

    private val _chatData = MutableStateFlow<List<ChatData>>(emptyList())
    private val _contextData = MutableStateFlow(Context())
    private val _planData = MutableStateFlow(Plan())

    val chatData = _chatData.asStateFlow()
    val contextData = _contextData.asStateFlow()
    val planData = _planData.asStateFlow()

    private suspend fun initChatMessage(
        index: Long,
        sendAble: (Boolean) -> Unit,
        isModify: Boolean
    ) {
        val coachMessages = index.toCoachMessage(isModify)
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

    fun initData(sendAble: (Boolean) -> Unit, isModify: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { dataStoreRepository.getUser() }
                .onSuccess {
                    coachIndex = it.coachNumber
                    initChatMessage(it.coachNumber, sendAble, isModify)
                    // TODO 나중에는 최신 러닝 데이터도 같이 넣어야 함!
                    val context = contextData.value.copy(userInfo = it.toUserInfo())
                    _contextData.emit(context)
                }

            runCatching { getPlanInfoUseCase() }
                .onSuccess { planInfo ->
                    val newPlanData = planInfo.toPlan()
                    _planData.emit(newPlanData)
                }.onFailure {
                    it.printStackTrace()
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
                .onSuccess { emitChatResponse(it) }
                .onFailure {
                    it.printStackTrace()
                    removeCoachChat()
                    failToMakeChat(it.message ?: FAILURE)
                }
        }

    private suspend fun removeCoachChat() {
        val newList = chatData.value.toMutableList()
        if (newList.lastOrNull() is ChatData.CoachData) newList.removeLast()
        _chatData.emit(newList)
    }

    private suspend fun emitChatResponse(chat: Chat) {
        val message = chat.message
        val coachChat = ChatData.CoachData(message, coachIndex)
        val newList = chatData.value.toMutableList()
        newList.removeLastOrNull()
        newList.add(coachChat)

        _chatData.emit(newList)
        _planData.emit(chat.plan)
        chat.context?.let { checkContext(it) }
    }

    private suspend fun checkContext(context: Context) = with(contextData.value.userInfo) {
        val emitUserInfo = context.userInfo
        val newUserInfo = UserInfo(
            age = emitUserInfo.age.ifBlank { age },
            height = emitUserInfo.height.ifZero { height },
            weight = emitUserInfo.weight.ifZero { weight },
            gender = emitUserInfo.gender.ifNotHuman { gender },
            injuries = emitUserInfo.injuries.ifEmpty { injuries },
            recentRunPace = emitUserInfo.recentRunPace.ifZero { recentRunPace },
            recentRunDistance = emitUserInfo.recentRunDistance.ifZero { recentRunDistance },
            recentRunHeartRate = emitUserInfo.recentRunHeartRate.ifZero { recentRunHeartRate }
        )

        val newContext = context.copy(userInfo = newUserInfo)
        _contextData.emit(newContext)
    }

    fun makePlan(
        uid: String,
        successToMakePlan: suspend () -> Unit,
        failToMakePlan: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val planRequest = PlanRequest(
            uid = uid,
            context = contextData.value,
            plan = planData.value
        )

        runCatching { makePlanUseCase(planRequest) }
            .onSuccess { successToMakePlan() }
            .onFailure {
                it.printStackTrace()
                failToMakePlan(ERROR)
            }
    }

    companion object {
        const val COACH_CHATTING = "AI 코치가 답변 중 입니다"
        const val SECONDS = 2
        const val FAILURE = "오류가 발생하였습니다. 잠시 후에 시도해주세요."
    }
}