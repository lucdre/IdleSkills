package com.lucdre.idleskills.cards.di

import com.lucdre.idleskills.cards.data.CardRepository
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import dagger.Binds
import dagger.Module
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
}
