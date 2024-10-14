package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class ModifyUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(uid: String, user: User): User {
        val response = userRepository.modify(uid, user)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        val responseUser = response.data ?: throw RuntimeException()
        val newUser = dataStoreRepository.getUser().copy(
            uid = uid,
            name = responseUser.name,
            age = responseUser.age,
            height = responseUser.height,
            weight = responseUser.weight,
            gender = responseUser.gender,
            injuries = responseUser.injuries,
            trainCount = responseUser.trainCount,
            trainTime = responseUser.trainTime,
            trainDistance = responseUser.trainDistance,
            coachNumber = responseUser.coachNumber
        )

        dataStoreRepository.saveUser(newUser)
        return newUser
    }
}
