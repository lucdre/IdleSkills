package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardCalculator
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Upgrade a card.
 *
 * @property cardRepository The repository managing card data.
 * @property inventoryRepository The repository managing player inventory.
 * @property cardCalculator The calculator for card formulas and requirements.
 */
class UpgradeCardUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface,
    private val inventoryRepository: InventoryRepositoryInterface,
    private val cardCalculator: CardCalculator
) {
    /**
     * Performs the card upgrade if requirements are met.
     *
     * @param card The card to upgrade.
     * @return Result indicating success or failure message.
     */
    suspend operator fun invoke(card: Card): Result<Unit> {
        val inventoryItems = inventoryRepository.observeItems().first()
        val requirements = cardCalculator.getUpgradeRequirements(card.type, card.level)
        
        if (requirements.isEmpty()) {
            return Result.failure(Exception("Max level reached."))
        }

        if (!cardCalculator.canUpgrade(card, inventoryItems)) {
            val firstMissing = requirements.find { req ->
                val owned = inventoryItems.find { it.type == req.itemType }?.quantity ?: 0
                owned < req.quantity
            }
            return Result.failure(Exception("Requires ${firstMissing?.quantity} ${firstMissing?.itemType?.displayName}."))
        }

        return try {
            // Consume all required resources
            requirements.forEach { req ->
                inventoryRepository.consumeItem(req.itemType, req.quantity)
            }

            // Perform the card update
            cardRepository.upgradeCard(
                card = card,
                nextLevel = card.level + 1,
                bonus = cardCalculator.getNextLevelBonus(card)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
