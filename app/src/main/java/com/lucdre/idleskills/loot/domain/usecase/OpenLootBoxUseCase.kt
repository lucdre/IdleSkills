package com.lucdre.idleskills.loot.domain.usecase

import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.loot.domain.LootGenerator
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import javax.inject.Inject

/**
 * Use case for opening a specific loot box.
 *
 * @property lootRepository The loot repository.
 * @property cardRepository The card repository.
 */
class OpenLootBoxUseCase @Inject constructor(
    private val lootRepository: LootRepositoryInterface,
    private val cardRepository: CardRepositoryInterface
) {
    /**
     * Attempts to open a loot box for a specific skill.
     *
     * @param skill The skill origin of the box to open.
     * @return Result containing the rewards if successful, or an error.
     */
    suspend operator fun invoke(skill: SkillType): Result<Map<CardType, Int>> {
        val success = lootRepository.consumeLootBox(skill)
        if (!success) {
            return Result.failure(Exception("No loot boxes available for ${skill.displayName}."))
        }

        val rewards = LootGenerator.generateRewards(skill, 20) //TODO change depending on the box potentially

        // Add rewards in batch
        cardRepository.addCardsBatch(rewards)

        return Result.success(rewards)
    }
}
