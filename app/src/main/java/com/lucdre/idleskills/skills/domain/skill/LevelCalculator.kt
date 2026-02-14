package com.lucdre.idleskills.skills.domain.skill

import kotlin.math.pow

/**
 * Utility object to calculate everything related to XP and skill levels.
 *
 * ## XP Formula: WIP
 */
object LevelCalculator {

    /**
     * (Placeholder)
     * Base XP required from level 1 to level 2.
     */
    private const val BASE_XP = 10

    /**
     * (Placeholder)
     * Exponential scaling factor to determine subsequent XP requirements to level up.
     */
    private const val SCALING_FACTOR = 1.1

    /**
     * Calculates XP required from current level to the next level.
     *
     * @param currentLevel The current level of the skill.
     * @return XP needed for the next level.
     */
    fun xpForNextLevel(currentLevel: Int): Int {
        return (BASE_XP * SCALING_FACTOR.pow(currentLevel - 1)).toInt()
    }

    /**
     * Calculates the skill level based on total XP.
     *
     * @param totalXp The total accumulated XP.
     * @return The current level based on total XP.
     */
    fun calculateLevelFromTotalXp(totalXp: Int): Int {
        var level = 1
        var xpAccumulated = 0

        while (true) {
            val xpForNext = xpForNextLevel(level)
            if (xpAccumulated + xpForNext <= totalXp) {
                xpAccumulated += xpForNext
                level++
            } else {
                break
            }
        }
        return level
    }

    /**
     * Calculates XP required to reach the next level from current total XP.
     *
     * @param currentTotalXp The total accumulated XP.
     * @param currentLevel The current level.
     * @return XP needed to reach the next level (how much more XP is needed beyond current total).
     */
    fun xpToNextLevelFromTotal(currentTotalXp: Int, currentLevel: Int): Int {
        val totalXpForNextLevel = totalXpForLevel(currentLevel + 1)
        return totalXpForNextLevel - currentTotalXp
    }

    /**
     * Checks if the skill level should be updated based on total XP.
     *
     *
     * @param skill The skill to check for level up.
     * @return The updated skill with the potentially new level, or the original skill in case of no level change.
     */
    fun checkForLevelUp(skill: Skill): Skill {
        val calculatedLevel = calculateLevelFromTotalXp(skill.xp)

        // If level changed, return updated skill
        return if (calculatedLevel != skill.level) {
            skill.copy(level = calculatedLevel)
        } else {
            skill
        }
    }

    /**
     * (Placeholder)
     * Calculate total XP required to reach a specific level from level 1.
     */
    fun totalXpForLevel(targetLevel: Int): Int {
        var totalXp = 0
        for (level in 1 until targetLevel) {
            totalXp += xpForNextLevel(level)
        }
        return totalXp
    }
}
