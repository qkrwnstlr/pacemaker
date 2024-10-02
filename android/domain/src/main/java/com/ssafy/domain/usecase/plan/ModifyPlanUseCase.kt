package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class ModifyPlanUseCase @Inject constructor(private val planRepository: PlanRepository) {

    suspend operator fun invoke(planRequest: PlanRequest) {
        val response = planRepository.modifyPlan(planRequest)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        return response.data ?: throw RuntimeException()
    }

}