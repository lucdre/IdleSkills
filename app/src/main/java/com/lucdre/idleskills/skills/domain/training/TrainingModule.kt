package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger/Hilt module providing the TrainingService.
 */
@Module
@InstallIn(SingletonComponent::class)
object TrainingModule {

    @Provides
    @Singleton
    fun provideTrainingService(
        managerFactory: SkillTrainingManager.Factory,
        getActiveCardsUseCase: GetActiveCardsUseCase,
        skillRepository: SkillRepositoryInterface,
        sessionRepository: SessionRepositoryInterface
    ): TrainingService {
        return TrainingService(
            managerFactory,
            getActiveCardsUseCase,
            skillRepository,
            sessionRepository
        )
    }
}
