package com.ssafy.data.di

import com.ssafy.data.repository.PlanRepositoryImpl
import com.ssafy.data.repository.TextToSpeechRepositoryImpl
import com.ssafy.data.repository.TrainRepositoryImpl
import com.ssafy.data.repository.UserRepositoryImpl
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.repository.TextToSpeechRepository
import com.ssafy.domain.repository.TrainRepository
import com.ssafy.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindPlanRepository(planRepositoryImpl: PlanRepositoryImpl): PlanRepository

    @Singleton
    @Binds
    abstract fun bindTrainRepository(trainRepositoryImpl: TrainRepositoryImpl): TrainRepository

    @Singleton
    @Binds
    abstract fun bindTextToSpeechRepository(textToSpeechRepositoryImpl: TextToSpeechRepositoryImpl): TextToSpeechRepository
}
