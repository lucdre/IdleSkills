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
     * Checks if you can level up based on your XP.
     *
     * Multiple level ups are possible and surplus XP is kept after all level ups are processed.
     *
     * @param skill The skill to check for level up.
     * @return The updated skill with the potentially new level and adjusted XP, or the original skill in case of no level up.
     */
    fun checkForLevelUp(skill: Skill): Skill {
        var currentLevel = skill.level
        var remainingXp = skill.xp
        var didLevelUp = false

        // Check for level ups until no more are possible
        while (true) {
            val xpRequired = xpForNextLevel(currentLevel)

            // Enough XP to level up?
            if (remainingXp >= xpRequired) {
                //Level up!
                currentLevel++
                remainingXp -= xpRequired
                didLevelUp = true
            } else {
                // No more level ups
                break
            }
        }

        // If no level up, return original skill
        if(!didLevelUp){
            return skill
        }

        return skill.copy(
            level = currentLevel,
            xp = remainingXp
        )
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