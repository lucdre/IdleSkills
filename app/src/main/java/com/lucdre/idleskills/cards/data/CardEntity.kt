package com.lucdre.idleskills.cards.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Database entity representing an owned card.
 *
 * @property cardType Unique identifier for the card type (corresponds to CardType enum name).
 * @property name User-facing name of the card.
 * @property quantity Current number of copies owned.
 * @property level Current upgrade level of the card.
 * @property efficiencyBonus Active percentage multiplier for boost.
 */
@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val cardType: String,
    val name: String,
    val quantity: Int,
    val level: Int,
    val efficiencyBonus: Float
)
