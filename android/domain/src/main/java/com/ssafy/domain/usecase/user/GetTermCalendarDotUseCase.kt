package com.ssafy.domain.usecase.user

import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import java.time.LocalDate
import javax.inject.Inject

class GetCalendarInfoUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        currentDate: LocalDate,
        range: Int = 2
    ): Map<String, List<ContentListDto>> {

        val uid = dataStoreRepository.getUser().uid
        val contentListMap = mutableMapOf<String, List<ContentListDto>>()

        (-range..range).forEach {
            val date = currentDate.plusMonths(it.toLong())
            val response = userRepository.getCalendarDot(uid, date.year, date.monthValue)
            if (response is ResponseResult.Error) throw RuntimeException(response.message)
            val contentListDto = response.data ?: throw RuntimeException()
            contentListMap.putAll(contentListDto)
        }
        return contentListMap
    }

}
