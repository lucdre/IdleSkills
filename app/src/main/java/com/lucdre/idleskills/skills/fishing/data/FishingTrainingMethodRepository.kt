package com.lucdre.idleskills.skills.fishing.data

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
 * Temporary repository that provides fishing training methods.
 */
@Singleton
class FishingTrainingMethodRepository @Inject constructor() : BaseTrainingMethodRepository() {

    override val trainingMethods = mapOf(
        SkillType.FISHING to listOf(
            TrainingMethod(TrainingMethodType.FS_SARDINE, 10, 3000, requiredCardType = CardType.FISHING_NET, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_SARDINE),
            TrainingMethod(TrainingMethodType.FS_ANCHOVY, 10, 3000, requiredCardType = CardType.FISHING_NET, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_ANCHOVY),
            TrainingMethod(TrainingMethodType.FS_TROUT, 20, 4000, 5, requiredCardType = CardType.FISHING_ROD, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_TROUT),
            TrainingMethod(TrainingMethodType.FS_SALMON, 40, 5000, 15, requiredCardType = CardType.FISHING_ROD, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_SALMON),
            TrainingMethod(TrainingMethodType.FS_TUNA, 80, 10000, 25, requiredCardType = CardType.FISHING_HARPOON, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_TUNA),
            TrainingMethod(TrainingMethodType.FS_LOBSTER, 100, 15000, 45, requiredCardType = CardType.FISHING_LOBSTER_CAGE, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_LOBSTER),
            TrainingMethod(TrainingMethodType.FS_SWORDFISH, 250, 20000, 60, requiredCardType = CardType.FISHING_HARPOON, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_SWORDFISH),
            TrainingMethod(TrainingMethodType.FS_SHARK, 350, 30000, 80, requiredCardType = CardType.FISHING_HARPOON, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_SHARK),
            TrainingMethod(TrainingMethodType.FS_CHEAT, 3000000, 1000, 1, requiredCardType = CardType.FISHING_ROD, availableRegions = listOf(Region.FIRST_REGION), producedItemType = ItemType.RAW_SARDINE)
        )
    )
}
