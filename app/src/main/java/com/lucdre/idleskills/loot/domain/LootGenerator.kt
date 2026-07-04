package com.lucdre.idleskills.loot.domain

import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import javax.inject.Inject
import kotlin.random.Random

/**
 * Result of a loot generation roll.
 *
 * @property items Map of items dropped and their quantities.
 * @property droppedBox The skill type of the loot box dropped, if any.
 */
data class LootReward(
    val items: Map<ItemType, Int> = emptyMap(),
    val droppedBox: SkillType? = null
)

/**
 * Handles the generation of rewards from loot sprites and boxes.
 *
 * This class centralizes all drop logic and rates.
 * 
 * @property random Injected randomness source for testability.
 */
class LootGenerator @Inject constructor(
    private val random: Random
) {

    companion object {
        // Resource drop configuration
        private const val MIN_RESOURCE_QTY = 1
        private const val MAX_RESOURCE_QTY = 10

        // Loot Box drop configuration
        private const val LOOT_BOX_CHANCE = 0.01 // 1% chance
        
        // Reward quantities
        private const val BOX_REWARD_QTY = 20

        // Item mappings for loot boxes
        private val woodcuttingItems = listOf(
            ItemType.NORMAL_LOGS, ItemType.OAK_LOGS, ItemType.WILLOW_LOGS,
            ItemType.MAPLE_LOGS, ItemType.YEW_LOGS, ItemType.MAGIC_LOGS
        )
        
        private val miningItems = listOf(
            ItemType.COPPER_ORE, ItemType.TIN_ORE, ItemType.IRON_ORE, ItemType.COAL,
            ItemType.MITHRIL_ORE, ItemType.ADAMANT_ORE, ItemType.RUNE_ORE, ItemType.DRAGON_ORE
        )
        
        private val fishingItems = listOf(
            ItemType.RAW_SHRIMP, ItemType.RAW_SARDINE, ItemType.RAW_ANCHOVY,
            ItemType.RAW_TROUT, ItemType.RAW_SALMON, ItemType.RAW_TUNA,
            ItemType.RAW_LOBSTER, ItemType.RAW_SWORDFISH, ItemType.RAW_SHARK
        )
    }

    /**
     * Generates rewards when a loot sprite is clicked.
     *
     * @param method The training method active when the sprite appeared.
     * @return A [LootReward] containing guaranteed resources and a rare chance for a box.
     */
    fun generateRewards(method: TrainingMethod): LootReward {
        val items = mutableMapOf<ItemType, Int>()
        
        // 1. Guaranteed resources from the current training method
        method.producedItemType?.let { itemType ->
            val quantity = random.nextInt(MIN_RESOURCE_QTY, MAX_RESOURCE_QTY + 1)
            items[itemType] = quantity
        }

        // 2. Rare chance for a skill-specific loot box
        val droppedBox = if (random.nextFloat() < LOOT_BOX_CHANCE) {
            method.skill
        } else {
            null
        }

        return LootReward(items = items, droppedBox = droppedBox)
    }

    /**
     * Generates rewards when opening a loot box.
     *
     * @param skill The skill type of the box being opened.
     * @return A map of rewards.
     */
    fun generateBoxRewards(skill: SkillType): Map<ItemType, Int> {
        val possibleItems = when (skill) {
            SkillType.WOODCUTTING -> woodcuttingItems
            SkillType.MINING -> miningItems
            SkillType.FISHING -> fishingItems
        }
        
        val randomItem = possibleItems[random.nextInt(possibleItems.size)]
        return mapOf(randomItem to BOX_REWARD_QTY)
    }
}
