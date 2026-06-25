package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import javax.inject.Inject

/**
 * Upgrade a card.
 * Checks requirements, bumps stats, and saves.
 *
 * @property cardRepository The repository managing card data.
 */
class UpgradeCardUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface
) {
    /**
     * Performs the card upgrade if requirements are met.
     *
     * @param card The card to upgrade.
     * @return Result indicating success or failure message.
     */
    suspend operator fun invoke(card: Card): Result<Unit> {
        if (!card.canUpgrade()) {
            return Result.failure(Exception("Requires ${card.getUpgradeRequirement()} cards."))
        }

        val upgradedCard = card.createUpgrade()
        cardRepository.updateCard(upgradedCard)
        
        return Result.success(Unit)
    }
}
