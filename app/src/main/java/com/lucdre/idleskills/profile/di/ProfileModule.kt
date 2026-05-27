package com.lucdre.idleskills.profile.di

import com.lucdre.idleskills.profile.data.ProfileRepository
import com.lucdre.idleskills.profile.data.StatisticsRepository
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        repository: ProfileRepository
    ): ProfileRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        repository: StatisticsRepository
    ): StatisticsRepositoryInterface
}
