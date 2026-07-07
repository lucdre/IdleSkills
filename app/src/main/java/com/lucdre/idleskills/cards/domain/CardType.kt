package com.lucdre.idleskills.cards.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.ui.graphics.vector.ImageVector
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
    val iconResId: Int,
    val iconTypeResId: ImageVector
) {
    WOODCUTTING_CARD(
        skill = SkillType.WOODCUTTING,
        rarity = "Common",
        displayName = "Woodcutting Speed",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.skill_woodcutting_axe,
        iconTypeResId = Icons.Default.Bolt
    ),
    MINING_CARD(
        skill = SkillType.MINING,
        rarity = "Common",
        displayName = "Mining Speed",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.skill_mining_pickaxe,
        iconTypeResId = Icons.Default.Bolt
    ),
    FISHING_CARD(
        skill = SkillType.FISHING,
        rarity = "Common",
        displayName = "Fishing Speed",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.skill_fishing_rod,
        iconTypeResId = Icons.Default.Bolt
    ),
}
