package com.ssafy.watch.di

import android.content.Context
import com.ssafy.watch.data.HealthServicesBackgroundRepository
import com.ssafy.watch.data.PassiveDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    fun providePassiveDataRepository(@ApplicationContext context: Context): PassiveDataRepository =
        PassiveDataRepository(context)

    @Provides
    fun provideHealthServiceRepository(@ApplicationContext context: Context): HealthServicesBackgroundRepository =
        HealthServicesBackgroundRepository(context)
}