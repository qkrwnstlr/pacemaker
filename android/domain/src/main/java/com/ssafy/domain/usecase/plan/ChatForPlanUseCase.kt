package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class ChatForPlanUseCase @Inject constructor(private val planRepository: PlanRepository) {

    suspend operator fun invoke(chat: Chat): ResponseResult<Chat> = planRepository.chatForPlan(chat)

}
