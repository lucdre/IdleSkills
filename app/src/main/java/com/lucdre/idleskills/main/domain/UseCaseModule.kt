package com.lucdre.idleskills.main.domain

import com.lucdre.idleskills.skills.domain.skill.usecase.GetSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillRepository
import com.lucdre.idleskills.skills.domain.training.usecase.GetTrainingMethodUseCase
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.tools.ToolRepositoryInterface
import com.lucdre.idleskills.skills.domain.tools.usecase.GetToolUseCase
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
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
    fun provideGetSkillsUseCase(repository: SkillRepository): GetSkillsUseCase {
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
    fun provideUpdateSkillUseCase(repository: SkillRepository): UpdateSkillUseCase {
        return UpdateSkillUseCase(repository)
    }

    /**
     * Provides a [GetTrainingMethodUseCase] instance.
     *
     * This use case retrieves available training methods for skills.
     *
     * @param repository The training method repository that provides the methods
     * @return A configured [GetTrainingMethodUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetTrainingMethodUseCase(repository: TrainingMethodRepositoryInterface): GetTrainingMethodUseCase {
        return GetTrainingMethodUseCase(repository)
    }

    /**
     * Provides a [GetToolUseCase] instance.
     *
     * This use case retrieves available tools for skills, which enhance training efficiency.
     *
     * @param repository The tool repository that provides the tools
     * @return A configured [GetToolUseCase]
     */
    @Provides
    @ViewModelScoped
    fun provideGetToolUseCase(repository: ToolRepositoryInterface): GetToolUseCase {
        return GetToolUseCase(repository)
    }
}