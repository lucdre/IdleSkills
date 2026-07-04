package com.lucdre.idleskills.loot.domain.usecase

import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.loot.domain.LootGenerator
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import javax.inject.Inject

/**
 * Use case for opening a specific loot box.
 *
 * @property lootRepository The loot repository.
 * @property inventoryRepository The inventory repository.
 * @property lootGenerator The loot generator.
 */
class OpenLootBoxUseCase @Inject constructor(
    private val lootRepository: LootRepositoryInterface,
    private val inventoryRepository: InventoryRepositoryInterface,
    private val lootGenerator: LootGenerator
) {
    /**
     * Attempts to open a loot box for a specific skill.
     *
     * @param skill The skill origin of the box to open.
     * @return Result containing the rewards if successful, or an error.
     */
    suspend operator fun invoke(skill: SkillType): Result<Map<ItemType, Int>> {
        val success = lootRepository.consumeLootBox(skill)
        if (!success) {
            return Result.failure(Exception("No loot boxes available for ${skill.displayName}."))
        }

        val rewards = lootGenerator.generateBoxRewards(skill)

        // Add rewards in batch
        rewards.forEach { (itemType, quantity) ->
            inventoryRepository.addItem(itemType, quantity)
        }

        return Result.success(rewards)
    }
}
