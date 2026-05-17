@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.main.data

import com.lucdre.idleskills.cards.data.CardRepository
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.data.MockPrestigeRepository
import com.lucdre.idleskills.skills.data.MockSkillRepository
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryDispatcher
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger/Hilt module containing repository implementations.
 *
 * Defines bindings between repository interfaces and their implementations
 * using @[Binds].
 * Repositories are singletons.
 * These repository bindings are requested by the use case module.
 *
 * ## Bindings:
 * - [TrainingMethodRepositoryInterface] to [TrainingMethodRepositoryDispatcher]
 * - [CardRepositoryInterface] to [CardRepository]
 * - [SkillRepositoryInterface] to [MockSkillRepository]
 * - [PrestigeRepositoryInterface] to [MockPrestigeRepository]
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrainingMethodRepositoryDispatcher(
        dispatcher: TrainingMethodRepositoryDispatcher
    ): TrainingMethodRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        repository: CardRepository
    ): CardRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        repository: MockSkillRepository
    ): SkillRepositoryInterface

    @Binds
    abstract fun bindPrestigeRepository(
        mockPrestigeRepository: MockPrestigeRepository
    ): PrestigeRepositoryInterface
}
