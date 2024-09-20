package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String, name: String?): ResponseResult<Unit> {
        val user = User(uid, name ?: RUNNER)
        return userRepository.signUp(user)
    }

    companion object {
        const val RUNNER = "런린이"
    }
}
