package com.lucdre.idleskills.loot.domain.usecase

import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import javax.inject.Inject

/**
 * Use case for collecting a new loot box.
 */
class CollectLootBoxUseCase @Inject constructor(
    private val lootRepository: LootRepositoryInterface
) {
    /**
     * Increments the count for a specific skill box.
     * 
     * @param skillName The skill origin of the box.
     */
    suspend operator fun invoke(skillName: String) {
        lootRepository.collectLootBox(skillName)
    }
}
