package com.lucdre.idleskills.skills.woodcutting.data

import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.BaseTrainingMethodRepository
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository that provides woodcutting training methods.
 *
 * Contains predefined training methods for the woodcutting skill
 * with XP amount, action durations, and level, card and region requirements.
 */
@Singleton
class WcTrainingMethodRepository @Inject constructor() : BaseTrainingMethodRepository() {

    override val trainingMethods = mapOf(
        SkillType.WOODCUTTING to listOf(
            TrainingMethod(SkillType.WOODCUTTING, "Tree", 10, 3000, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION)), // 10 XP, 3 seconds, Level 1
            TrainingMethod(SkillType.WOODCUTTING, "Oak Tree", 15, 4000, 10, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION)), // 15 XP, 4 seconds, Level 10
            TrainingMethod(SkillType.WOODCUTTING, "Willow Tree", 22, 5000, 25, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION, Region.THIRD_REGION)),
            TrainingMethod(SkillType.WOODCUTTING, "Maple Tree", 40, 8000, 45, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION)),
            TrainingMethod(SkillType.WOODCUTTING, "Yew Tree", 80, 12000, 60, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION)),
            TrainingMethod(SkillType.WOODCUTTING, "Magic Tree", 100, 20000, 75, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION)),
            TrainingMethod(SkillType.WOODCUTTING, "Cheat Tree", 3000000, 1000, 1, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION)) // Cheat
        )
    )
}
