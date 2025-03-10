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
import com.ssafy.domain.usecase.plan.ModifyPlanUseCase
import com.ssafy.domain.utils.ifZero
import com.ssafy.presentation.core.healthConnect.HealthConnectManager
import com.ssafy.presentation.planUI.registerPlan.adapter.ChatData
import com.ssafy.presentation.utils.ERROR
import com.ssafy.presentation.utils.displayText
import com.ssafy.presentation.utils.ifNotHuman
import com.ssafy.presentation.utils.toCoachMessage
import com.ssafy.presentation.utils.toDayString
import com.ssafy.presentation.utils.toPlan
import com.ssafy.presentation.utils.toUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class RegisterPlanViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatForPlanUseCase: ChatForPlanUseCase,
    private val makePlanUseCase: MakePlanUseCase,
    private val modifyPlanUseCase: ModifyPlanUseCase,
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {

    private var coachIndex: Long = 1
    var isModify: Boolean = false

    private val _chatData = MutableStateFlow<List<ChatData>>(emptyList())
    private val _contextData = MutableStateFlow(Context())
    private val _planData = MutableStateFlow(Plan())
    private val _failEvent = MutableSharedFlow<String>()

    val chatData = _chatData.asStateFlow()
    val contextData = _contextData.asStateFlow()
    val planData = _planData.asStateFlow()
    val failEvent = _failEvent.asSharedFlow()

    private suspend fun initChatMessage(
        index: Long,
        sendAble: (Boolean) -> Unit,
        showSelectWeekDayDialog: () -> Unit,
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
                showSelectWeekDayDialog()
            }
        }.collect { coachMessage ->
            val newList = _chatData.value.toMutableList()
            newList.add(coachMessage)
            _chatData.emit(newList)
        }
    }

    fun initData(
        sendAble: (Boolean) -> Unit,
        showSelectWeekDayDialog: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { dataStoreRepository.getUser() }
                .onSuccess {
                    coachIndex = it.coachNumber
                    initChatMessage(it.coachNumber, sendAble, showSelectWeekDayDialog)

                    var userInfo = it.toUserInfo()

                    if (healthConnectManager.hasAllPermissions()) {
                        val end = ZonedDateTime.now()
                        val start = end.minusDays(30)
                        val running = healthConnectManager.readExerciseSessions(
                            start.toInstant(),
                            end.toInstant()
                        ).map { session ->
                            healthConnectManager.readAssociatedSessionData(session.metadata.id)
                        }.lastOrNull { sessionData ->
                            sessionData.totalActiveTime != null && sessionData.totalDistance != null && sessionData.totalDistance.inKilometers != 0.0
                        }

                        userInfo = userInfo.copy(
                            recentRunDistance = running?.totalDistance?.inMeters?.toInt()
                                ?: userInfo.recentRunDistance,
                            recentRunHeartRate = running?.avgHeartRate?.toInt()
                                ?: userInfo.recentRunHeartRate,
                            recentRunPace = if (running?.totalActiveTime != null && running.totalDistance != null) {
                                (running.totalActiveTime.seconds / running.totalDistance.inKilometers).toInt()
                            } else {
                                userInfo.recentRunPace
                            },
                        )
                    }

                    val context = contextData.value.copy(userInfo = userInfo)
                    _contextData.emit(context)
                }
        }

        if (isModify) {
            viewModelScope.launch(Dispatchers.IO) {
                runCatching { getPlanInfoUseCase() }
                    .onSuccess { planInfo ->
                        val newPlanData = planInfo.toPlan(false)
                        val trainDays = planInfo.context.trainDayOfWeek
                        val newContext = contextData.value.copy(trainDayOfWeek = trainDays)

                        _planData.emit(newPlanData)
                        _contextData.emit(newContext)
                    }.onFailure {
                        it.printStackTrace()
                    }
            }
        }
    }

    fun sendMyMessage(message: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val newList = _chatData.value.toMutableList()
            val myMessage = ChatData.MyData(message)
            val coachMessage = ChatData.CoachData(COACH_CHATTING, coachIndex)
            newList.add(myMessage)
            newList.add(coachMessage)
            _chatData.emit(newList)
            makeChat(message)
        }

    private fun makeChat(text: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val chat = Chat(
                message = text,
                context = contextData.value,
                plan = planData.value,
                isModify = isModify
            )

            runCatching { chatForPlanUseCase(chat) }
                .onSuccess { emitChatResponse(it) }
                .onFailure {
                    it.printStackTrace()
                    removeCoachChat()
                    _failEvent.emit(it.message ?: FAILURE)
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
        successToMakePlan: suspend () -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        val planRequest = PlanRequest(
            context = contextData.value,
            plan = planData.value
        )

        runCatching { if (isModify) modifyPlanUseCase(planRequest) else makePlanUseCase(planRequest) }
            .onSuccess { successToMakePlan() }
            .onFailure {
                it.printStackTrace()
                _failEvent.emit(it.message ?: ERROR)
            }

        runCatching {
            with(contextData.value.userInfo) {
                healthConnectManager.writeWeightInput(weight.toDouble())
                healthConnectManager.writeHeightInput(height.toDouble())
            }
        }
    }

    fun setPlanDate(planWeek: Set<DayOfWeek>) {
        val newContext = _contextData.value.copy(trainDayOfWeek = planWeek.map { it.toDayString() })
        _contextData.update { newContext }

        val chat = planWeek.joinToString(", ") { it.displayText(locale = Locale.KOREAN) }
        sendMyMessage("훈련 요일을 ${chat}으로 설정할게")
    }

    companion object {
        const val COACH_CHATTING = "AI 코치가 답변 중 입니다"
        const val SECONDS = 2
        const val FAILURE = "오류가 발생하였습니다. 잠시 후에 시도해주세요."
    }
}