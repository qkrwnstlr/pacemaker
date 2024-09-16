package com.ssafy.watch.di

import android.content.Context
import com.ssafy.watch.data.HealthServicesRepository
import com.ssafy.watch.data.PassiveDataRepository
import com.ssafy.watch.utils.HealthServicesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    fun providePassiveDataRepository(@ApplicationContext context: Context): PassiveDataRepository =
        PassiveDataRepository(context)

    @Provides
    fun provideHealthServiceRepository(@ApplicationContext context: Context): HealthServicesRepository =
        HealthServicesRepository(context)
}