package com.ssafy.watch.presentation.di

import android.content.Context
import com.ssafy.watch.presentation.utils.HealthServicesManager
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
    fun provideHealthServices(@ApplicationContext context: Context): HealthServicesManager =
        HealthServicesManager(context)
}