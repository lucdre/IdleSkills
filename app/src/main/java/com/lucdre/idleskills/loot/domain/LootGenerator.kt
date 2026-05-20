package com.lucdre.idleskills.loot.domain

import com.lucdre.idleskills.cards.domain.CardType
import kotlin.random.Random

/**
 * Handles the generation of rewards from loot boxes.
 */
object LootGenerator {

    /**
     * Configuration for loot drops.
     *
     * @property cardType The type of card that can drop.
     * @property weight Relative weight for this card type to drop.
     */
    data class DropConfig(
        val cardType: CardType,
        val weight: Int
    )

    private val dropConfigs = listOf(
        DropConfig(CardType.WOODCUTTING_AXE, 10),
        DropConfig(CardType.MINING_PICKAXE, 10),
        DropConfig(CardType.FISHING_NET, 10),
        DropConfig(CardType.FISHING_ROD, 10),
        DropConfig(CardType.FISHING_HARPOON, 10),
        DropConfig(CardType.FISHING_LOBSTER_CAGE, 10)
    )

    /**
     * Generates a random set of rewards based on the box's origin skill.
     *
     * @param originSkill The skill name the loot box came from. 
     *                   Bias will be applied to cards of this skill.
     * @param amountToDrop Number of card drops to generate.
     * @return A map of [CardType] to quantity dropped.
     */
    fun generateRewards(originSkill: String, amountToDrop: Int): Map<CardType, Int> {
        val rewards = mutableMapOf<CardType, Int>()
        
        // Apply bias: Cards matching the origin skill are 10x more likely to drop
        val weightedConfigs = dropConfigs.map { config ->
            val finalWeight = if (config.cardType.skillName == originSkill) {
                config.weight * 10
            } else {
                config.weight
            }
            config to finalWeight
        }

        val totalWeight = weightedConfigs.sumOf { it.second }

        repeat(amountToDrop) {
            val randomValue = Random.nextInt(totalWeight)
            var currentWeight = 0
            
            for ((config, weight) in weightedConfigs) {
                currentWeight += weight
                if (randomValue < currentWeight) {
                    val currentCount = rewards[config.cardType] ?: 0
                    rewards[config.cardType] = currentCount + 1
                    break
                }
            }
        }
        
        return rewards
    }
}
