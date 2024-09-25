package com.ssafy.data.repository

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toResponseResult
import com.ssafy.data.source.train.TrainDataSource
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import com.ssafy.domain.repository.TrainRepository
import com.ssafy.domain.response.ResponseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainRepositoryImpl @Inject constructor(
    private val trainDataSource: TrainDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TrainRepository {
    override suspend fun getCoaching(dto: CoachingRequest): ResponseResult<CoachingResponse> {
        val response = withContext(ioDispatcher) { trainDataSource.getCoaching(dto) }
        return response.toResponseResult()
    }
}