package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetCoachUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String): ResponseResult<Coach> = userRepository.getCoach(uid)
}
