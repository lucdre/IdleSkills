package com.lucdre.idleskills.profile.di

import com.lucdre.idleskills.core.persistence.AppDatabase
import com.lucdre.idleskills.profile.data.PreferencesDao
import com.lucdre.idleskills.profile.data.PreferencesRepository
import com.lucdre.idleskills.profile.data.ProfileDao
import com.lucdre.idleskills.profile.data.ProfileRepository
import com.lucdre.idleskills.profile.data.StatisticsRepository
import com.lucdre.idleskills.profile.domain.PreferencesRepositoryInterface
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
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
    abstract fun bindPreferencesRepository(
        repository: PreferencesRepository
    ): PreferencesRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        repository: StatisticsRepository
    ): StatisticsRepositoryInterface

    companion object {
        @Provides
        fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()

        @Provides
        fun providePreferencesDao(db: AppDatabase): PreferencesDao = db.preferencesDao()
    }
}
