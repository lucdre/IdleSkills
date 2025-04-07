package com.lucdre.idleskills.skills.domain.usecase

import com.lucdre.idleskills.skills.domain.TrainingMethod
import com.lucdre.idleskills.skills.domain.TrainingMethodRepositoryInterface
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

        // Return the highest requirement method (likely the best one)
        return availableMethods.maxByOrNull { it.requiredLevel }
    }
}