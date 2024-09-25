package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class ChatForPlanUseCase @Inject constructor(private val planRepository: PlanRepository) {

    suspend operator fun invoke(chat: Chat): ResponseResult<Chat> {
        val newPlanTrains = chat.plan.planTrains.map {
            val repeat = if (it.repeat == 0) 1 else it.repeat
            it.copy(repeat = repeat)
        }
        val newPlan = chat.plan.copy(planTrains = newPlanTrains)
        val newChat = chat.copy(plan = newPlan)
        return planRepository.chatForPlan(newChat)
    }

}
