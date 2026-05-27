package com.lucdre.idleskills.loot.domain

import com.lucdre.idleskills.skills.domain.skill.SkillType
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
     * @param skill The origin skill of the collected box.
     */
    suspend fun collectLootBox(skill: SkillType)

    /**
     * Decrements the loot box count for a specific skill.
     * 
     * @param skill The origin skill of the box to consume.
     * @return True if a box was successfully consumed.
     */
    suspend fun consumeLootBox(skill: SkillType): Boolean
}
