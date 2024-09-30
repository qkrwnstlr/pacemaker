package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class SetCoachUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(coachIndex: Long) {
        val user = dataStoreRepository.getUser()
        val uid = user.uid
        val coach = Coach(coachIndex)
        val response = userRepository.setCoach(uid, coach)

        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        dataStoreRepository.saveUser(user.copy(coachNumber = coachIndex))
    }

}
