package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.skills.fishing.domain.FishingToolRequirements
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to retrieve all owned cards relevant to a specific skill and training method.
 *
 * It filters cards based on:
 * 1. Skill compatibility (e.g., Woodcutting cards for Woodcutting skill).
 * 2. Training method compatibility (e.g., Fishing Rod card only for Rod Fishing methods).
 */
class GetActiveCardsUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface
) {
    operator fun invoke(skillName: String, methodName: String?): Flow<List<Card>> {
        return cardRepository.getCardsForSkill(skillName).map { cards ->
            if (methodName == null) {
                // If no method, return all cards for the skill (e.g., for general display)
                cards
            } else {
                // Filter based on training method requirements
                filterCompatibleCards(cards, skillName, methodName)
            }
        }
    }

    private fun filterCompatibleCards(
        cards: List<Card>,
        skillName: String,
        methodName: String
    ): List<Card> {
        return when (skillName.lowercase()) {
            "fishing" -> {
                val requiredType = FishingToolRequirements.getRequiredCardType(methodName)
                cards.filter { card ->
                    // Fishing cards must match the required card type for the fish
                    isFishingCardCompatible(card.type, requiredType)
                }
            }
            "woodcutting" -> cards.filter { it.type == CardType.WOODCUTTING_AXE }
            "mining" -> cards.filter { it.type == CardType.MINING_PICKAXE }
            else -> cards
        }
    }

    private fun isFishingCardCompatible(
        cardType: CardType,
        requiredCardType: CardType?
    ): Boolean {
        if (requiredCardType == null) return true
        return cardType == requiredCardType
    }
}
