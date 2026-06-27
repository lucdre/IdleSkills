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
        if (!cardCalculator.canUpgrade(card)) {
            val requirement = cardCalculator.getUpgradeRequirement(card.level)
            return Result.failure(Exception("Requires $requirement cards."))
        }

        val upgradedCard = cardCalculator.createUpgrade(card)
        cardRepository.updateCard(upgradedCard)
        
        return Result.success(Unit)
    }
}
