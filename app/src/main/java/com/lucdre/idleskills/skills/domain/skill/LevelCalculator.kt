package com.lucdre.idleskills.skills.domain.skill

import kotlin.math.pow

object LevelCalculator {

    // Base xp required for level 1->2
    private const val BASE_XP = 10

    // Exponential scaling factor
    private const val SCALING_FACTOR = 1.1

    /**
     * XP required for next level
     */
    fun xpForNextLevel(currentLevel: Int): Int {
        return (BASE_XP * SCALING_FACTOR.pow(currentLevel - 1)).toInt()
    }

    /**
     * Check for level up
     *
     * Multiple level ups are possible and surplus XP is kept after all level ups are processed.
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
     * Calculate total XP required to reach a specific level from level 1, for the future
     */
    fun totalXpForLevel(targetLevel: Int): Int {
        var totalXp = 0
        for (level in 1 until targetLevel) {
            totalXp += xpForNextLevel(level)
        }
        return totalXp
    }
}