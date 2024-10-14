package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class ModifyPlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(planRequest: PlanRequest) {
        val uid = dataStoreRepository.getUser().uid
        val newPlanRequest = planRequest.copy(uid = uid)
        val response = planRepository.modifyPlan(newPlanRequest)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        return response.data ?: throw RuntimeException()
    }

}