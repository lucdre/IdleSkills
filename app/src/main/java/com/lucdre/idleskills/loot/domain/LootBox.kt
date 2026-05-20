package com.lucdre.idleskills.loot.domain

/**
 * Represents a loot box that players can collect and open.
 *
 * @property skillName The name of the skill this box originated from.
 * @property count The number of boxes of this type owned.
 */
data class LootBox(
    val skillName: String,
    val count: Int = 0
) {
    /**
     * @return A custom name for the loot box based on its origin skill.
     */
    fun getDisplayName(): String {
        return when (skillName) {
            "Woodcutting" -> "Bird's Nest"
            "Mining" -> "Geode"
            "Fishing" -> "Treasure Chest"
            else -> "$skillName Mystery Box"
        }
    }
}
