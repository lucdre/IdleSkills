package com.lucdre.idleskills.prestige.di

import com.lucdre.idleskills.prestige.data.PrestigeRepository
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrestigeModule {

    @Binds
    @Singleton
    abstract fun bindPrestigeRepository(
        repository: PrestigeRepository
    ): PrestigeRepositoryInterface
}
