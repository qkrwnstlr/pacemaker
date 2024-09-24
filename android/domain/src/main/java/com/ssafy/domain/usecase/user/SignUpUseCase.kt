package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.LoginRequestBody
import com.ssafy.domain.dto.LoginResponseBody
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String, name: String?): ResponseResult<LoginResponseBody> {
        return userRepository.signUp(uid, LoginRequestBody(name ?: RUNNER))
    }

    companion object {
        const val RUNNER = "런린이"
    }
}
