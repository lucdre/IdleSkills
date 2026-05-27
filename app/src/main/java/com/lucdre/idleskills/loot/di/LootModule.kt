package com.lucdre.idleskills.loot.di

import com.lucdre.idleskills.loot.data.LootRepository
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LootModule {

    @Binds
    @Singleton
    abstract fun bindLootRepository(
        repository: LootRepository
    ): LootRepositoryInterface
}
