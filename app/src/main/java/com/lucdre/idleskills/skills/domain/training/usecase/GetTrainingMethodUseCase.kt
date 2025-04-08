package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject

class GetTrainingMethodUseCase @Inject constructor(
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