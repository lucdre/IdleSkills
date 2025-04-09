package com.lucdre.idleskills.skills.woodcutting.data

import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository that provides woodcutting training methods.
 *
 * Contains predefined training methods for the woodcutting skill
 * with XP amount, action durations, and level requirements.
 */
@Singleton
class WcTrainingMethodRepository @Inject constructor(): TrainingMethodRepositoryInterface{

    private val trainingMethods = mapOf(
        "Woodcutting" to listOf(
            TrainingMethod("Woodcutting", "Tree", 10, 10000), // 10 XP, 10 seconds, Level 1
            TrainingMethod("Woodcutting", "Oak Tree", 15, 10000, 5), //15 XP, 10 seconds, Level 5
            TrainingMethod("Woodcutting", "Willow Tree", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Cheat Tree", 300, 1000, 1)// Cheat
        )
    )

    /**
     * Retrieves training methods available for the specified skill.
     *
     * @param skillName The name of the skill to get training methods for
     * @return List of training methods available for the skill, or empty list if skill not found
     */
    override fun getTrainingMethodsForSkill(skillName: String): List<TrainingMethod> {
        return trainingMethods[skillName] ?: emptyList()
    }
}