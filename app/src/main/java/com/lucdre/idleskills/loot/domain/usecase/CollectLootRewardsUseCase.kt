package com.lucdre.idleskills.loot.domain.usecase

import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.loot.domain.LootGenerator
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.loot.domain.LootReward
import com.lucdre.idleskills.skills.domain.training.TrainingService
import javax.inject.Inject

/**
 * Orchestrates the collection of rewards from a loot sprite.
 *
 * @property lootGenerator Generator for determining rewards.
 * @property inventoryRepository Repository to add resource rewards.
 * @property lootRepository Repository to add rare loot boxes.
 * @property trainingService Service to get the current training context.
 */
class CollectLootRewardsUseCase @Inject constructor(
    private val lootGenerator: LootGenerator,
    private val inventoryRepository: InventoryRepositoryInterface,
    private val lootRepository: LootRepositoryInterface,
    private val trainingService: TrainingService
) {
    /**
     * Collects rewards based on the current training method.
     * 
     * @return The [LootReward] collected, or null if no training is active.
     */
    suspend operator fun invoke(): LootReward? {
        val activeMethod = trainingService.trainingState.value.activeMethod ?: return null
        
        val reward = lootGenerator.generateRewards(activeMethod)
        
        // 1. Add resource items
        reward.items.forEach { (itemType, quantity) ->
            inventoryRepository.addItem(itemType, quantity)
        }
        
        // 2. Add rare loot box if dropped
        reward.droppedBox?.let { skillType ->
            lootRepository.collectLootBox(skillType)
        }

        return reward
    }
}
