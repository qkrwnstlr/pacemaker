package com.ssafy.data.di

import com.ssafy.data.repository.DataStoreRepositoryImpl
import com.ssafy.domain.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStoreRepository(dataStoreRepositoryImpl: DataStoreRepositoryImpl): DataStoreRepository {
        return dataStoreRepositoryImpl
    }
}