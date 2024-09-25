package com.ssafy.domain.usecase.plan

import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class DeletePlanUseCase @Inject constructor(private val planRepository: PlanRepository) {

    suspend operator fun invoke(uid: String): ResponseResult<Unit> = planRepository.deletePlan(uid)

}
