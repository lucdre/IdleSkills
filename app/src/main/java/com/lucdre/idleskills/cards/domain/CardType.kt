package com.lucdre.idleskills.cards.domain

import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Categories of cards in the game.
 *
 * Each type contains metadata for its default state and progression.
 */
enum class CardType(
    val skill: SkillType,
    val rarity: String,
    val displayName: String,
    val initialEfficiencyBonus: Float,
    val bonusPerLevel: Float,
    val iconResId: Int
) {
    WOODCUTTING_CARD(
        skill = SkillType.WOODCUTTING,
        rarity = "Common",
        displayName = "Woodcutting Speed",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    MINING_CARD(
        skill = SkillType.MINING,
        rarity = "Common",
        displayName = "Mining Speed",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    FISHING_CARD(
        skill = SkillType.FISHING,
        rarity = "Common",
        displayName = "Fishing Speed",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
}
