package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject

/**
 * Use case for retrieving training methods for a specific skill.
 *
 * @property trainingMethodRepository The repository for training methods.
 * @property profileRepository The repository for player profile data.
 *
 * Methods:
 * - [invoke]: Get all available training methods for a specific skill in the current region.
 * - [getBestAvailableMethod]: Returns the best available training method for a skill at the current level in the current region. (Placeholder)
 */
class GetTrainingMethodUseCase @Inject constructor(
    private val trainingMethodRepository: TrainingMethodRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface
) {
    suspend operator fun invoke(skillName: String): List<TrainingMethod> {
        val region = profileRepository.getProfile().currentRegion
        val skill = SkillType.fromString(skillName) ?: SkillType.fromString(skillName) ?: return emptyList()
        return trainingMethodRepository.getTrainingMethodsForSkill(skill, region)
    }

    suspend fun getBestAvailableMethod(skillName: String, currentLevel: Int): TrainingMethod? {
        val region = profileRepository.getProfile().currentRegion
        val skill = SkillType.fromString(skillName) ?: SkillType.fromString(skillName) ?: return null
        val methods = trainingMethodRepository.getTrainingMethodsForSkill(skill, region)

        // Get all methods that user has the level for
        val availableMethods = methods.filter { it.requiredLevel <= currentLevel }

        // Return the highest requirement method (likely the best one, temporary only)
        return availableMethods.maxByOrNull { it.requiredLevel }
    }
}
