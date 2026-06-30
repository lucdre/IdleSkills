package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import javax.inject.Inject

/**
 * Use case for recording a completed training action.
 *
 * @property statisticsRepository The repository where the statistics are stored.
 */
class RecordTrainingActionUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepositoryInterface
) {
    /**
     * Records completed training actions for a specific skill and method.
     *
     * @param skill The skill being trained.
     * @param methodType The type of the training method used.
     * @param amount The number of actions completed.
     */
    suspend operator fun invoke(skill: SkillType, methodType: TrainingMethodType, amount: Int = 1) {
        statisticsRepository.incrementCount(skill, methodType.id, amount)
    }
}
