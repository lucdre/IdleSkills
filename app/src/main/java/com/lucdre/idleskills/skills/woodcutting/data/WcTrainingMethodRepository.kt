package com.lucdre.idleskills.skills.woodcutting.data

import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.region.domain.Region
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
class WcTrainingMethodRepository @Inject constructor() : TrainingMethodRepositoryInterface {

    private val trainingMethods = mapOf(
        "Woodcutting" to listOf(
            TrainingMethod("Woodcutting", "Tree", 10, 3000, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION)), // 10 XP, 3 seconds, Level 1
            TrainingMethod("Woodcutting", "Oak Tree", 15, 4000, 10, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION)), // 15 XP, 4 seconds, Level 10
            TrainingMethod("Woodcutting", "Willow Tree", 22, 5000, 25, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION, Region.THIRD_REGION)),
            TrainingMethod("Woodcutting", "Maple Tree", 40, 8000, 45, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION)),
            TrainingMethod("Woodcutting", "Yew Tree", 80, 12000, 60, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION)),
            TrainingMethod("Woodcutting", "Magic Tree", 100, 20000, 75, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION)),
            TrainingMethod("Woodcutting", "Cheat Tree", 3000000, 1000, 1, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION)) // Cheat
        )
    )

    /**
     * Retrieves training methods available for the specified skill in a given region.
     *
     * @param skillName The name of the skill to get training methods for
     * @param region The region for which to fetch training methods
     * @return List of training methods available for the skill and region, or empty list if not found
     */
    override fun getTrainingMethodsForSkill(skillName: String, region: Region): List<TrainingMethod> {
        return (trainingMethods[skillName] ?: emptyList()).filter { 
            it.availableRegions.isEmpty() || region in it.availableRegions 
        }
    }
}
