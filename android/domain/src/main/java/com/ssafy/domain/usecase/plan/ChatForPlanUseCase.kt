package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.utils.toMakeFeature
import javax.inject.Inject

class ChatForPlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(chat: Chat): Chat {
        val newPlanTrains = chat.plan.planTrains.map {
            val repeat = if (it.repetition == 0) 1 else it.repetition
            it.copy(repetition = repeat)
        }
        val newPlan = chat.plan.copy(planTrains = newPlanTrains)
        val feature = dataStoreRepository.getUser().toMakeFeature()
        val newChat = chat.copy(plan = newPlan, coachTone = feature)

        val response = if (!chat.isModify) {
            planRepository.chatForPlan(newChat)
        } else {
            planRepository.chatForModifyPlan(newChat)
        }
        
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        return response.data ?: throw RuntimeException()
    }
}
