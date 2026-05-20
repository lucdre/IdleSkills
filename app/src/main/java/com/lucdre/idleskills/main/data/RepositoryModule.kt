@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.main.data

import com.lucdre.idleskills.cards.data.CardRepository
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.prestige.data.MockPrestigeRepository
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.loot.data.LootRepository
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.profile.data.ProfileRepository
import com.lucdre.idleskills.profile.data.StatisticsRepository
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
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
 * - [ProfileRepositoryInterface] to [ProfileRepository]
 * - [StatisticsRepositoryInterface] to [StatisticsRepository]
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the [TrainingMethodRepositoryDispatcher] to the [TrainingMethodRepositoryInterface].
     * The dispatcher routes requests to skill-specific repositories using Hilt multi-bindings.
     */
    @Binds
    @Singleton
    abstract fun bindTrainingMethodRepositoryDispatcher(
        dispatcher: TrainingMethodRepositoryDispatcher
    ): TrainingMethodRepositoryInterface

    /**
     * Binds the [CardRepository] implementation to the [CardRepositoryInterface].
     */
    @Binds
    @Singleton
    abstract fun bindCardRepository(
        repository: CardRepository
    ): CardRepositoryInterface

    /**
     * Binds the [MockSkillRepository] implementation to the [SkillRepositoryInterface].
     */
    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        repository: MockSkillRepository
    ): SkillRepositoryInterface

    /**
     * Binds the [ProfileRepository] implementation to the [ProfileRepositoryInterface].
     */
    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        repository: ProfileRepository
    ): ProfileRepositoryInterface

    /**
     * Binds the [StatisticsRepository] implementation to the [StatisticsRepositoryInterface].
     */
    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        repository: StatisticsRepository
    ): StatisticsRepositoryInterface

    /**
     * Binds the [MockPrestigeRepository] implementation to the [PrestigeRepositoryInterface].
     */
    @Binds
    abstract fun bindPrestigeRepository(
        mockPrestigeRepository: MockPrestigeRepository
    ): PrestigeRepositoryInterface

    /**
     * Binds the [LootRepository] implementation to the [LootRepositoryInterface].
     */
    @Binds
    @Singleton
    abstract fun bindLootRepository(
        repository: LootRepository
    ): LootRepositoryInterface
}
