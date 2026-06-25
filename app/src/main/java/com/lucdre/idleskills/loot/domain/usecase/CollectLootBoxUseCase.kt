package com.lucdre.idleskills.loot.domain.usecase

import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import javax.inject.Inject

/**
 * Collects a new loot box.
 */
class CollectLootBoxUseCase @Inject constructor(
    private val lootRepository: LootRepositoryInterface
) {
    /**
     * Increments the count for a specific skill box.
     * 
     * @param skill The skill origin of the box.
     */
    suspend operator fun invoke(skill: SkillType) {
        lootRepository.collectLootBox(skill)
    }
}
