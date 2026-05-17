package com.lucdre.idleskills.skills.mining.data

import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository that provides mining training methods.
 *
 * Contains predefined training methods for the mining skill
 * with XP amount, action durations, and level requirements.
 */
@Singleton
class MiningTrainingMethodRepository @Inject constructor() : TrainingMethodRepositoryInterface {

    private val trainingMethods = mapOf(
        "Mining" to listOf(
            TrainingMethod("Mining", "Copper Rock", 10, 3000, requiredCardType = CardType.MINING_PICKAXE), // 10 XP, 3 seconds, Level 1
            TrainingMethod("Mining", "Tin Rock", 10, 3000, requiredCardType = CardType.MINING_PICKAXE), // 15 XP, 4 seconds, Level 1
            TrainingMethod("Mining", "Iron Rock", 20, 4000, 5, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod("Mining", "Coal Rock", 40, 5000, 15, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod("Mining", "Mithril Rock", 80, 10000, 25, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod("Mining", "Adamant Rock", 100, 15000, 45, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod("Mining", "Rune Rock", 250, 20000, 60, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod("Mining", "Dragon Rock", 350, 30000, 80, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod("Mining", "Cheat Rock", 3000000, 1000, 1, requiredCardType = CardType.MINING_PICKAXE) // Cheat
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
