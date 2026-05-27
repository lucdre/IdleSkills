package com.lucdre.idleskills.loot.domain

import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Represents a loot box that players can collect and open.
 *
 * @property skill The skill this box originated from.
 * @property count The number of boxes of this type owned.
 */
data class LootBox(
    val skill: SkillType,
    val count: Int = 0
) {
    /**
     * @return A custom name for the loot box based on its origin skill.
     */
    fun getDisplayName(): String {
        return when (skill) {
            SkillType.WOODCUTTING -> "Bird's Nest"
            SkillType.MINING -> "Geode"
            SkillType.FISHING -> "Treasure Chest"
        }
    }
}
