package com.ssafy.domain.repository

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.domain.response.ResponseResult

interface PlanRepository {

    suspend fun chatForPlan(chat: Chat): ResponseResult<Chat>

    suspend fun chatForModifyPlan(chat: Chat): ResponseResult<Chat>

    suspend fun getProgress(uid:String, year:Int, month:Int, day:Int): ResponseResult<ProgressData>

    suspend fun makePlan(planRequest: PlanRequest): ResponseResult<Unit>

    suspend fun getPlan(uid: String): ResponseResult<PlanInfo>

    suspend fun modifyPlan(planRequest: PlanRequest) : ResponseResult<Unit>

    suspend fun deletePlan(uid: String): ResponseResult<Unit>

}
