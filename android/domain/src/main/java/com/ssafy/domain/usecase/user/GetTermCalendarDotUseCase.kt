package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetTermCalendarDotUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        uid: String,
        startYear: Int,
        startMonth: Int,
        currentYear: Int,
        currentMonth: Int,
        endYear: Int,
        endMonth: Int
    ): Map<String, List<ContentListDto>> {

        val startResponse = userRepository.getCalendarDot(uid, startYear, startMonth)
        val currentResponse = userRepository.getCalendarDot(uid, currentYear, currentMonth)
        val endResponse = userRepository.getCalendarDot(uid, endYear, endMonth)
        if (startResponse is ResponseResult.Error || currentResponse is ResponseResult.Error || endResponse is ResponseResult.Error) throw RuntimeException()
        val dotList = mutableMapOf<String, List<ContentListDto>>()
        startResponse.data?.let { sit->
            dotList.putAll(sit)
            currentResponse.data?.let { cit ->
                dotList.putAll(cit)
                endResponse.data?.let{ eit ->
                    dotList.putAll(eit)
                }
            }
        } ?: throw RuntimeException()
        return dotList
    }
}