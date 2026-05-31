package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
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
     * Records a completed training action for a specific skill and method.
     *
     * @param skill The skill being trained.
     * @param methodName The name of the training method used.
     */
    suspend operator fun invoke(skill: SkillType, methodName: String) {
        statisticsRepository.incrementCount(skill, methodName)
    }
}
