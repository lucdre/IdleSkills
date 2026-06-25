package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import javax.inject.Inject

/**
 * Get available training methods.
 */
class GetAvailableTrainingMethodsUseCase @Inject constructor(
    private val getTrainingMethodUseCase: GetTrainingMethodUseCase
) {
    /**
     * Returns a list of training methods for the given skill that are available at its current level.
     */
    suspend operator fun invoke(skill: Skill): List<TrainingMethod> {
        val allMethods = getTrainingMethodUseCase(skill.type.name)
        return allMethods.filter { it.requiredLevel <= skill.level }
    }
}
