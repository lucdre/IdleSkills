package com.lucdre.idleskills.region.di

import com.lucdre.idleskills.region.data.RegionRepository
import com.lucdre.idleskills.region.domain.RegionRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegionModule {

    @Binds
    @Singleton
    abstract fun bindRegionRepository(
        repository: RegionRepository
    ): RegionRepositoryInterface
}
