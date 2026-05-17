package com.lucdre.idleskills.skills.fishing.data

import com.lucdre.idleskills.cards.domain.CardType
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
            TrainingMethod("Fishing", "Sardine", 10, 3000, requiredCardType = CardType.FISHING_NET), // 10 XP, 3 seconds, Level 1
            TrainingMethod("Fishing", "Anchovy", 10, 3000, requiredCardType = CardType.FISHING_NET),
            TrainingMethod("Fishing", "Trout", 20, 4000, 5, requiredCardType = CardType.FISHING_ROD),
            TrainingMethod("Fishing", "Salmon", 40, 5000, 15, requiredCardType = CardType.FISHING_ROD),
            TrainingMethod("Fishing", "Tuna", 80, 10000, 25, requiredCardType = CardType.FISHING_HARPOON),
            TrainingMethod("Fishing", "Lobster", 100, 15000, 45, requiredCardType = CardType.FISHING_LOBSTER_CAGE),
            TrainingMethod("Fishing", "Swordfish", 250, 20000, 60, requiredCardType = CardType.FISHING_HARPOON),
            TrainingMethod("Fishing", "Shark", 350, 30000, 80, requiredCardType = CardType.FISHING_HARPOON),
            TrainingMethod("Fishing", "Cheat Fish", 3000000, 1000, 1, requiredCardType = CardType.FISHING_ROD) // Cheat
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
