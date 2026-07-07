package com.lucdre.idleskills.cards.domain

import androidx.compose.runtime.Immutable

/**
 * A card that provides bonuses to skill training.
 *
 * @property name The display name of the card.
 * @property type The type/category of the card.
 * @property level The current level of the card.
 * @property quantity Number of copies owned of this specific card level.
 * @property efficiencyBonus The efficiency bonus provided by this card
 */
@Immutable
data class Card(
    val name: String,
    val type: CardType,
    val level: Int = 1,
    val quantity: Int = 1,
    val efficiencyBonus: Float = 0f,
)
