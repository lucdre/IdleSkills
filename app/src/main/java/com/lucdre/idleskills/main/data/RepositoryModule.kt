@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.main.data

import com.lucdre.idleskills.skills.data.MockSkillRepository
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.tools.ToolRepositoryInterface
import com.lucdre.idleskills.skills.woodcutting.data.WcTrainingMethodRepository
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import com.lucdre.idleskills.skills.woodcutting.data.WcToolRepository
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
 * - [TrainingMethodRepositoryInterface] to [WcTrainingMethodRepository]
 * - [ToolRepositoryInterface] to [WcToolRepository]
 * - [SkillRepositoryInterface] to [MockSkillRepository]
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrainingMethodRepository(
        repository: WcTrainingMethodRepository
    ): TrainingMethodRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindToolRepository(
        repository: WcToolRepository
    ): ToolRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        repository: MockSkillRepository
    ): SkillRepositoryInterface
}