package com.lucdre.idleskills.loot.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing skill-specific loot boxes.
 */
interface LootRepositoryInterface {
    /**
     * Observes the list of all owned loot boxes.
     */
    fun observeLootBoxes(): Flow<List<LootBox>>

    /**
     * Increments the loot box count for a specific skill.
     * 
     * @param skillName The origin skill of the collected box.
     */
    suspend fun collectLootBox(skillName: String)

    /**
     * Decrements the loot box count for a specific skill.
     * 
     * @param skillName The origin skill of the box to consume.
     * @return True if a box was successfully consumed.
     */
    suspend fun consumeLootBox(skillName: String): Boolean
}
