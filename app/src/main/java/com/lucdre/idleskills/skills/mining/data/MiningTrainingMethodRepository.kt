package com.lucdre.idleskills.skills.mining.data

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
 * Temporary repository that provides mining training methods.
 */
@Singleton
class MiningTrainingMethodRepository @Inject constructor() : BaseTrainingMethodRepository() {

    override val trainingMethods = mapOf(
        SkillType.MINING to listOf(
            TrainingMethod(TrainingMethodType.MN_COPPER, 6, 5000, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.COPPER_ORE),
            TrainingMethod(TrainingMethodType.MN_TIN, 6, 5000, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.TIN_ORE),
            TrainingMethod(TrainingMethodType.MN_IRON, 25, 12000, 15, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.IRON_ORE),
            TrainingMethod(TrainingMethodType.MN_COAL, 40, 5000, 15, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.COAL),
            TrainingMethod(TrainingMethodType.MN_MITHRIL, 80, 10000, 25, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.MITHRIL_ORE),
            TrainingMethod(TrainingMethodType.MN_ADAMANT, 100, 15000, 45, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.ADAMANT_ORE),
            TrainingMethod(TrainingMethodType.MN_RUNE, 250, 20000, 60, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.RUNE_ORE),
            TrainingMethod(TrainingMethodType.MN_DRAGON, 350, 30000, 80, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.THIRD_REGION), producedItemType = ItemType.DRAGON_ORE),
            TrainingMethod(TrainingMethodType.MN_CHEAT, 3000000, 1000, 1, requiredCardType = CardType.MINING_PICKAXE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.COPPER_ORE)
        )
    )
}
