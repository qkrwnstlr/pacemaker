package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.CheckUid
import com.ssafy.domain.dto.Exist
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class CheckUidUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String): ResponseResult<Exist> {
        val checkUid = CheckUid(uid)
        return userRepository.checkUid(checkUid)
    }
}
