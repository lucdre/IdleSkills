package com.lucdre.idleskills.skills.fishing.data

import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository that provides fishing training methods.
 *
 * Contains predefined training methods for the fishing skill
 * with XP amount, action durations, and level requirements.
 */
@Singleton
class FishingTrainingMethodRepository @Inject constructor() : TrainingMethodRepositoryInterface {

    private val trainingMethods = mapOf(
        "Fishing" to listOf(
            TrainingMethod("Fishing", "Sardine", 10, 3000), // 10 XP, 3 seconds, Level 1
            TrainingMethod("Fishing", "Anchovy", 10, 3000),
            TrainingMethod("Fishing", "Trout", 20, 4000, 5),
            TrainingMethod("Fishing", "Salmon", 40, 5000, 15),
            TrainingMethod("Fishing", "Tuna", 80, 10000, 25),
            TrainingMethod("Fishing", "Lobster", 100, 15000, 45),
            TrainingMethod("Fishing", "Swordfish", 250, 20000, 60),
            TrainingMethod("Fishing", "Shark", 350, 30000, 80),
            TrainingMethod("Fishing", "Cheat Fish", 3000000, 1000, 1) // Cheat
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
