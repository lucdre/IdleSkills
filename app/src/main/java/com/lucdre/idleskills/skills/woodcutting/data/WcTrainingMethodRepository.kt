package com.lucdre.idleskills.skills.woodcutting.data

import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.BaseTrainingMethodRepository
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository that provides woodcutting training methods.
 */
@Singleton
class WcTrainingMethodRepository @Inject constructor() : BaseTrainingMethodRepository() {

    override val trainingMethods = mapOf(
        SkillType.WOODCUTTING to listOf(
            TrainingMethod(TrainingMethodType.WC_TREE, 10, 3000, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.NORMAL_LOGS),
            TrainingMethod(TrainingMethodType.WC_OAK, 15, 4000, 10, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.OAK_LOGS),
            TrainingMethod(TrainingMethodType.WC_WILLOW, 22, 5000, 25, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION, Region.THIRD_REGION), producedItemType = ItemType.WILLOW_LOGS),
            TrainingMethod(TrainingMethodType.WC_MAPLE, 40, 8000, 45, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.MAPLE_LOGS),
            TrainingMethod(TrainingMethodType.WC_YEW, 80, 12000, 60, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.YEW_LOGS),
            TrainingMethod(TrainingMethodType.WC_MAGIC, 100, 20000, 75, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.MAGIC_LOGS),
            TrainingMethod(TrainingMethodType.WC_CHEAT, 3000000, 1000, 1, requiredCardType = CardType.WOODCUTTING_AXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.NORMAL_LOGS)
        )
    )
}
