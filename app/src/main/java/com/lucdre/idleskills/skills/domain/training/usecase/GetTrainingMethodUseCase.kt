package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject

/**
 * Use case for retrieving training methods for a specific skill.
 *
 * @property trainingMethodRepository The repository for training methods.
 * @property sessionRepository The repository for session data.
 */
class GetTrainingMethodUseCase @Inject constructor(
    private val trainingMethodRepository: TrainingMethodRepositoryInterface,
    private val sessionRepository: SessionRepositoryInterface
) {
    suspend operator fun invoke(skillName: String): List<TrainingMethod> {
        val region = sessionRepository.getCurrentRegion()
        val skill = SkillType.fromString(skillName) ?: return emptyList()
        return trainingMethodRepository.getTrainingMethodsForSkill(skill, region)
    }
}
