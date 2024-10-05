package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    suspend operator fun invoke(uid: String? = null): User {
        val userId = uid ?: dataStoreRepository.getUser().uid
        val response = userRepository.getInfo(userId)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        val user = response.data ?: throw RuntimeException()
        val newUser = user.copy(uid = userId)
        dataStoreRepository.saveUser(newUser)
        return response.data
    }

}
