package com.lucdre.idleskills.skills.domain.skill

/**
 * Basic representation of a skill.
 *
 * Immutable data object. To modify it, creating a new instance using [copy].
 *
 * @property name The name of the skill.
 * @property level The level of the skill.
 * @property xp the experience points of the skill.
 */
data class Skill(
    val name: String,
    val level: Int = 1, // Default level to 1
    val xp: Int = 0 // Default XP to 0
)