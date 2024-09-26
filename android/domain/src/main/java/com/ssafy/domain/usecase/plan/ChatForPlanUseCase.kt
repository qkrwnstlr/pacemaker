package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.User
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.utils.DANNY
import com.ssafy.domain.utils.DANNY_FEAT
import com.ssafy.domain.utils.JAMIE
import com.ssafy.domain.utils.JAMIE_FEAT
import com.ssafy.domain.utils.MIKE
import com.ssafy.domain.utils.MIKE_FEAT
import javax.inject.Inject

class ChatForPlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(chat: Chat): ResponseResult<Chat> {
        val newPlanTrains = chat.plan.planTrains.map {
            val repeat = if (it.repetition == 0) 1 else it.repetition
            it.copy(repetition = repeat)
        }
        val newPlan = chat.plan.copy(planTrains = newPlanTrains)
        val feature = dataStoreRepository.getUser().toMakeFeature()
        val newChat = chat.copy(plan = newPlan, coachTone = feature)

        return planRepository.chatForPlan(newChat)
    }

    private fun User.toMakeFeature(): String = when (coachNumber) {
        MIKE -> MIKE_FEAT
        JAMIE -> JAMIE_FEAT
        DANNY -> DANNY_FEAT
        else -> ""
    }

}
