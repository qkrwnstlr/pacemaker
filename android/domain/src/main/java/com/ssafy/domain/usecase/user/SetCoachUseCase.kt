package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class SetCoachUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String, coach: Coach): ResponseResult<Unit> =
        userRepository.setCoach(uid, coach)
}
