package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.LoginRequestBody
import com.ssafy.domain.dto.LoginResponseBody
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(uid: String, name: String?): LoginResponseBody {
        val response = userRepository.signUp(uid, LoginRequestBody(name ?: RUNNER))
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        val user = response.data?.userInfoResponse ?: throw RuntimeException()
        val newUser = user.copy(uid = uid)
        dataStoreRepository.saveUser(newUser)
        return response.data
    }

    companion object {
        const val RUNNER = "런린이"
    }
}
