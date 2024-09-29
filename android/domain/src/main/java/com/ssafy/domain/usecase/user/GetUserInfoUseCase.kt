package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String): User {
        val response = userRepository.getInfo(uid)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)
        return response.data ?: throw RuntimeException()
    }
}
