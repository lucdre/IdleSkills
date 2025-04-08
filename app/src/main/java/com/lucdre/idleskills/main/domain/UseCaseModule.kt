package com.lucdre.idleskills.main.domain

import com.lucdre.idleskills.skills.domain.skill.usecase.GetSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillRepository
import com.lucdre.idleskills.skills.domain.training.usecase.GetTrainingMethodUseCase
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetSkillsUseCase(repository: SkillRepository): GetSkillsUseCase {
        return GetSkillsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateSkillUseCase(repository: SkillRepository): UpdateSkillUseCase {
        return UpdateSkillUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTrainingMethodUseCase(repository: TrainingMethodRepositoryInterface): GetTrainingMethodUseCase {
        return GetTrainingMethodUseCase(repository)
    }
}