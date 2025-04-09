package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface

/**
 * Use case for retrieving training methods for a specific skill.
 *
 * @property trainingMethodRepository The repository for training methods.
 *
 * Methods:
 * - [invoke]: Get all available training methods for a specific skill.
 * - [getBestAvailableMethod]: Returns the best available training method for a skill at the current level. (Placeholder)
 */
class GetTrainingMethodUseCase (
    private val trainingMethodRepository: TrainingMethodRepositoryInterface
) {
    operator fun invoke(skillName: String): List<TrainingMethod> {
        return trainingMethodRepository.getTrainingMethodsForSkill(skillName)
    }

    fun getBestAvailableMethod(skillName: String, currentLevel: Int): TrainingMethod? {
        val methods = trainingMethodRepository.getTrainingMethodsForSkill(skillName)

        // Get all methods that user has the level for
        val availableMethods = methods.filter { it.requiredLevel <= currentLevel }

        // Return the highest requirement method (likely the best one, temporary only)
        return availableMethods.maxByOrNull { it.requiredLevel }
    }
}