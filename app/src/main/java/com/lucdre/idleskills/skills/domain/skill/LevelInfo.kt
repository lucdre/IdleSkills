package com.lucdre.idleskills.skills.domain.skill

/**
 * Information about a skill's level and XP progress.
 */
data class LevelInfo(
    val currentLevel: Int = 1,
    val totalXp: Int = 0,
    val nextLevelXp: Int = 0,
    val xpToNextLevel: Int = 0,
    val progressDecimal: Float = 0f
)
