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
    val displayName: String,
    val initialEfficiencyBonus: Float,
    val bonusPerLevel: Float,
    val iconResId: Int
) {
    WOODCUTTING_AXE(
        skill = SkillType.WOODCUTTING,
        displayName = "Bronze Axe",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    MINING_PICKAXE(
        skill = SkillType.MINING,
        displayName = "Bronze Pickaxe",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    FISHING_NET(
        skill = SkillType.FISHING,
        displayName = "Small Fishing Net",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    FISHING_ROD(
        skill = SkillType.FISHING,
        displayName = "Fishing Rod",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    FISHING_HARPOON(
        skill = SkillType.FISHING,
        displayName = "Harpoon",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    ),
    FISHING_LOBSTER_CAGE(
        skill = SkillType.FISHING,
        displayName = "Lobster Cage",
        initialEfficiencyBonus = 0.00f,
        bonusPerLevel = 0.05f,
        iconResId = R.drawable.ic_tree
    );

    /**
     * @param level The current level of the card.
     * @return The number of cards required to upgrade to the next level.
     */
    fun getUpgradeRequirement(level: Int): Int {
        return level * 10
    }
}
