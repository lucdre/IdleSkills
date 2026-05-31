package com.lucdre.idleskills.skills.domain.skill

/**
 * Basic representation of a skill.
 *
 * Immutable data object. To modify it, creating a new instance using [copy].
 *
 * @property type The type of the skill.
 * @property level The level of the skill.
 * @property xp the experience points of the skill.
 */
data class Skill(
    val type: SkillType,
    val level: Int = 1, // Default level to 1
    val xp: Int = 0 // Default XP to 0
) {
    /**
     * The internal identifier of the skill (Enum name).
     */
    val name: String get() = type.name

    /**
     * The human-readable display name of the skill.
     */
    val displayName: String get() = type.displayName
}
