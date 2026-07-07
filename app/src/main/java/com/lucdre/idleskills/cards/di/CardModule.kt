@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.cards.di

import com.lucdre.idleskills.cards.data.CardDao
import com.lucdre.idleskills.cards.data.CardRepository
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.core.persistence.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CardModule {

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        repository: CardRepository
    ): CardRepositoryInterface

    companion object {
        @Provides
        fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()
    }
}
