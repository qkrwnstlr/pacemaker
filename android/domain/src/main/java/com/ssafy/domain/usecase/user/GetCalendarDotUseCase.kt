package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetCalendarDotUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        uid: String,
        year: Int,
        month: Int
    ): Map<String, List<ContentListDto>> {
        val response = userRepository.getCalendarDot(uid, year, month)
        if (response is ResponseResult.Error) throw RuntimeException()
        response.data?.let { dotList -> return dotList } ?: throw RuntimeException()
    }
}