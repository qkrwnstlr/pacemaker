package com.ssafy.data.di

import com.ssafy.data.source.plan.PlanDataSource
import com.ssafy.data.source.plan.PlanDataSourceImpl
import com.ssafy.data.source.reports.ReportsDataSource
import com.ssafy.data.source.reports.ReportsDataSourceImpl
import com.ssafy.data.source.train.TrainDataSource
import com.ssafy.data.source.train.TrainDataSourceImpl
import com.ssafy.data.source.user.UserDataSource
import com.ssafy.data.source.user.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Singleton
    @Binds
    abstract fun bindPlanDataSource(planDataSourceImpl: PlanDataSourceImpl): PlanDataSource

    @Singleton
    @Binds
    abstract fun bindTrainDataSource(trainDataSourceImpl: TrainDataSourceImpl): TrainDataSource

    @Singleton
    @Binds
    abstract fun bindReportsDataSource(reportDataSourceImpl: ReportsDataSourceImpl): ReportsDataSource

}
