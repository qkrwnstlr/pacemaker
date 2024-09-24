package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class ModifyUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String, user: User): ResponseResult<User> =
        userRepository.modify(uid, user)
}
