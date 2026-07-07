@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.inventory.di

import com.lucdre.idleskills.core.persistence.AppDatabase
import com.lucdre.idleskills.inventory.data.InventoryDao
import com.lucdre.idleskills.inventory.data.InventoryRepository
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InventoryModule {

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepository: InventoryRepository
    ): InventoryRepositoryInterface

    companion object {
        @Provides
        fun provideInventoryDao(database: AppDatabase): InventoryDao {
            return database.inventoryDao()
        }
    }
}
