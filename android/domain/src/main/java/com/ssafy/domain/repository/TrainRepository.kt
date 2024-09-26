package com.ssafy.domain.repository

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import com.ssafy.domain.response.ResponseResult

interface TrainRepository {
    suspend fun getCoaching(dto: CoachingRequest): ResponseResult<CoachingResponse>
}