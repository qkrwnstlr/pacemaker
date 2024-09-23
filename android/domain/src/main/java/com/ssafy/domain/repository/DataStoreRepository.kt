package com.ssafy.domain.repository

import com.ssafy.domain.dto.User
import kotlinx.coroutines.flow.Flow


interface DataStoreRepository {
    suspend fun setImgUrl(newImgUrl: String)
    suspend fun saveUser(user: User)
    fun getImgUrl(): Flow<String>
    suspend fun clearImgUrl()
}