package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.User
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.plan.UserInfo
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.utils.ifZero
import javax.inject.Inject

class MakePlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(body: PlanRequest) {
        val uid = dataStoreRepository.getUser().uid
        val response = planRepository.makePlan(body.copy(uid = uid))
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        val prevUser = dataStoreRepository.getUser()
        val newUser = body.context.userInfo.toUser(prevUser)
        dataStoreRepository.saveUser(newUser)
    }

    private fun UserInfo.toUser(prevUser: User) = User(
        uid = prevUser.uid,
        name = prevUser.name,
        cadence = prevUser.cadence,
        distance = prevUser.distance,
        minute = prevUser.minute,
        pace = prevUser.pace,
        height = height.ifZero { prevUser.height },
        weight = weight.ifZero { prevUser.weight },
        age = age.toIntOrNull() ?: prevUser.age,
        trainCount = prevUser.trainCount,
        trainTime = prevUser.trainTime,
        trainDistance = prevUser.trainDistance,
        gender = gender.ifBlank { prevUser.gender },
        coachNumber = prevUser.coachNumber,
        injuries = injuries.ifEmpty { prevUser.injuries }
    )

}
