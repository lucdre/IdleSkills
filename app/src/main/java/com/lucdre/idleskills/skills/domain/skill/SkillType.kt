package com.lucdre.idleskills.skills.domain.skill

/**
 * Enum representing all skills in the game.
 */
enum class SkillType(val displayName: String) {
    WOODCUTTING("Woodcutting"),
    MINING("Mining"),
    FISHING("Fishing");

    companion object {
        fun fromString(name: String): SkillType? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
