package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardCalculator
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import javax.inject.Inject

/**
 * Upgrade a card.
 * Checks requirements, bumps stats, and saves.
 *
 * @property cardRepository The repository managing card data.
 * @property cardCalculator The calculator for card formulas.
 */
class UpgradeCardUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface,
    private val cardCalculator: CardCalculator
) {
    /**
     * Performs the card upgrade if requirements are met.
     *
     * @param card The card to upgrade.
     * @return Result indicating success or failure message.
     */
    suspend operator fun invoke(card: Card): Result<Unit> {
        val requirement = cardCalculator.getUpgradeRequirement(card.level)
        
        if (!cardCalculator.canUpgrade(card)) {
            return Result.failure(Exception("Requires $requirement cards."))
        }

        return try {
            cardRepository.upgradeCard(
                card = card,
                requirement = requirement,
                nextLevel = card.level + 1,
                bonus = cardCalculator.getNextLevelBonus(card)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
