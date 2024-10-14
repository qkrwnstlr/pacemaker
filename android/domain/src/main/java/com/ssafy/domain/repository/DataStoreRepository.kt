package com.ssafy.domain.repository

import com.ssafy.domain.dto.User
import kotlinx.coroutines.flow.Flow


interface DataStoreRepository {
    suspend fun setImgUrl(newImgUrl: String)
    suspend fun saveUser(user: User)
    fun getImgUrl(): Flow<String>
    suspend fun clearImgUrl()
    suspend fun getUser(): User
    suspend fun setLocation(latitude: Double, longitude: Double)
    fun getLatitude(): Flow<Double>
    fun getLongitude(): Flow<Double>
}