@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.loot.di

import com.lucdre.idleskills.core.persistence.AppDatabase
import com.lucdre.idleskills.loot.data.LootBoxDao
import com.lucdre.idleskills.loot.data.LootRepository
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
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

    companion object {
        @Provides
        fun provideLootBoxDao(db: AppDatabase): LootBoxDao = db.lootBoxDao()
    }
}
