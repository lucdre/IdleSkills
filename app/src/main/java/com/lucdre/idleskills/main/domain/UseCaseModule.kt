package com.lucdre.idleskills.main.domain

import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.cards.domain.usecase.UpgradeCardUseCase
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.domain.usecase.CheckPrestigeRequirementsUseCase
import com.lucdre.idleskills.prestige.domain.usecase.GetPrestigeStateUseCase
import com.lucdre.idleskills.region.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.prestige.domain.usecase.PerformPrestigeUseCase
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.profile.domain.usecase.GetPlayerProfileUseCase
import com.lucdre.idleskills.profile.domain.usecase.IsGameFreshUseCase
import com.lucdre.idleskills.profile.domain.usecase.SetupPlayerProfileUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.usecase.GetSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.usecase.ResetSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.usecase.GetTrainingMethodUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Dagger/Hilt module providing use case instances.
 *
 * Installed in the [ViewModelComponent] providing use cases scoped to ViewModels.
 * A new instance will be created for each ViewModel.
 *
 * All use cases are [ViewModelScoped] ensuring they are reused within the same ViewModel
 * but not shared between different ViewModels.
 *
 * Dagger flow:
 * - A ViewModel requests a use case via @inject constructor.
 * - Dagger checks if it can be provided by [UseCaseModule] (if it has a @Provides annotation).
 * - The repository module provides a repository for it.
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    /**
     * Provides a [GetSkillsUseCase] instance.
     *
     * This use case retrieves skill data and observes changes to skills.
     *
     * @param repository The skill repository that provides the data
     * @return A configured [GetSkillsUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetSkillsUseCase(repository: SkillRepositoryInterface): GetSkillsUseCase {
        return GetSkillsUseCase(repository)
    }

    /**
     * Provides an [UpdateSkillUseCase] instance.
     *
     * This use case handles updating skill XP and levels.
     *
     * @param repository The skill repository used to persist skill updates
     * @return A configured [UpdateSkillUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideUpdateSkillUseCase(repository: SkillRepositoryInterface): UpdateSkillUseCase {
        return UpdateSkillUseCase(repository)
    }

    /**
     * Provides a [GetTrainingMethodUseCase] instance.
     *
     * This use case retrieves available training methods for skills.
     *
     * @param repository The training method repository that provides the methods
     * @param profileRepository The profile repository to check current region
     * @return A configured [GetTrainingMethodUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetTrainingMethodUseCase(
        repository: TrainingMethodRepositoryInterface,
        profileRepository: ProfileRepositoryInterface
    ): GetTrainingMethodUseCase {
        return GetTrainingMethodUseCase(repository, profileRepository)
    }

    /**
     * Provides a [GetActiveCardsUseCase] instance.
     *
     * @param repository The card repository
     * @param trainingMethodRepository The training method repository
     * @param profileRepository The profile repository
     * @return A configured [GetActiveCardsUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetActiveCardsUseCase(
        repository: CardRepositoryInterface,
        trainingMethodRepository: TrainingMethodRepositoryInterface,
        profileRepository: ProfileRepositoryInterface
    ): GetActiveCardsUseCase {
        return GetActiveCardsUseCase(repository, trainingMethodRepository, profileRepository)
    }

    /**
     * Provides an [UpgradeCardUseCase] instance.
     *
     * @param repository The card repository
     * @return A configured [UpgradeCardUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideUpgradeCardUseCase(
        repository: CardRepositoryInterface
    ): UpgradeCardUseCase {
        return UpgradeCardUseCase(repository)
    }

    /**
     * Provides a [SetupPlayerProfileUseCase] instance.
     *
     * @param profileRepository The profile repository
     * @param prestigeRepository The prestige repository
     * @return A configured [SetupPlayerProfileUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideSetupPlayerProfileUseCase(
        profileRepository: ProfileRepositoryInterface
    ): SetupPlayerProfileUseCase {
        return SetupPlayerProfileUseCase(profileRepository)
    }

    /**
     * Provides an [IsGameFreshUseCase] instance.
     *
     * @param repository The profile repository
     * @return A configured [IsGameFreshUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideIsGameFreshUseCase(repository: ProfileRepositoryInterface): IsGameFreshUseCase {
        return IsGameFreshUseCase(repository)
    }

    /**
     * Provides a [GetPlayerProfileUseCase] instance.
     *
     * @param repository The profile repository
     * @return A configured [GetPlayerProfileUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetPlayerProfileUseCase(repository: ProfileRepositoryInterface): GetPlayerProfileUseCase {
        return GetPlayerProfileUseCase(repository)
    }

    /**
     * Provides a [ResetSkillsUseCase] instance.
     *
     * This use case handles resetting all skills to their initial state (level 1, 0 XP).
     * Used during prestige operations to reset player progress.
     *
     * @param skillRepository The skill repository used to persist skill resets
     * @return A configured [ResetSkillsUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideResetSkillsUseCase(
        skillRepository: SkillRepositoryInterface
    ): ResetSkillsUseCase {
        return ResetSkillsUseCase(skillRepository)
    }


    /**
     * Provides a [CheckPrestigeRequirementsUseCase] instance.
     *
     * This use case validates whether the player meets the requirements to prestige
     * based on their current prestige level and skill levels.
     *
     * @param skillRepository The skill repository to check skill levels
     * @param prestigeRepository The prestige repository to check current prestige level
     * @return A configured [CheckPrestigeRequirementsUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideCheckPrestigeRequirementsUseCase(
        skillRepository: SkillRepositoryInterface
    ): CheckPrestigeRequirementsUseCase {
        return CheckPrestigeRequirementsUseCase(skillRepository)
    }

    /**
     * Provides a [GetVisibleSkillsUseCase] instance.
     *
     * This use case filters skills based on prestige level to control which skills
     * are visible to the player. Implements the progressive skill unlocking system.
     *
     * @param skillRepository The skill repository that provides all skills
     * @param prestigeRepository The prestige repository to check visibility rules
     * @param profileRepository The profile repository to check current region
     * @return A configured [GetVisibleSkillsUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetVisibleSkillsUseCase(
        skillRepository: SkillRepositoryInterface,
        profileRepository: ProfileRepositoryInterface
    ): GetVisibleSkillsUseCase {
        return GetVisibleSkillsUseCase(skillRepository, profileRepository)
    }

    /**
     * Provides a [PerformPrestigeUseCase] instance.
     *
     * This use case orchestrates the complete prestige operation: checking requirements,
     * resetting skills, and incrementing prestige level.
     *
     * @param prestigeRepository The prestige repository to update prestige state
     * @param skillRepository The skill repository that provides all skills
     * @param getPrestigeStateUseCase Use case to see the complete state of a prestige
     * @param resetSkillsUseCase Use case to reset all skills during prestige
     * @return A configured [PerformPrestigeUseCase]
     */
    @Provides
    @ViewModelScoped
    fun providePerformPrestigeUseCase(
        prestigeRepository: PrestigeRepositoryInterface,
        skillRepository: SkillRepositoryInterface,
        getPrestigeStateUseCase: GetPrestigeStateUseCase,
        resetSkillsUseCase: ResetSkillsUseCase
    ): PerformPrestigeUseCase {
        return PerformPrestigeUseCase(prestigeRepository, skillRepository, getPrestigeStateUseCase, resetSkillsUseCase)
    }

    /**
     * Provides a [GetPrestigeStateUseCase] instance.
     *
     * This use case combines stored prestige data with real-time requirement checking
     * to provide complete prestige state information for UI display
     * (Prestige level and if you can prestige).
     *
     * @param prestigeRepository The prestige repository to get current prestige level
     * @param checkPrestigeRequirementsUseCase Use case to check if prestiging is possible
     * @return A configured [GetPrestigeStateUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetPrestigeStateUseCase(
        prestigeRepository: PrestigeRepositoryInterface,
        checkPrestigeRequirementsUseCase: CheckPrestigeRequirementsUseCase
    ): GetPrestigeStateUseCase {
        return GetPrestigeStateUseCase(prestigeRepository, checkPrestigeRequirementsUseCase)
    }
}
