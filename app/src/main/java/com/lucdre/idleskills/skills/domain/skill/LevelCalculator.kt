package com.lucdre.idleskills.skills.domain.skill

import kotlin.math.floor
import kotlin.math.pow

import com.lucdre.idleskills.core.util.Constants
import com.lucdre.idleskills.main.presentation.LevelInfo

/**
 * Utility object to calculate everything related to XP and skill levels.
 *
 */
object LevelCalculator {

    /**
     * Maximum level.
     */
    private const val MAX_LEVEL = Constants.MAX_LEVEL

    /**
     * Maximum XP.
     */
    private const val MAX_XP = Constants.MAX_XP

    /**
     * Precomputed XP table for levels 1 to 126.
     */
    private val xpTable: IntArray = IntArray(MAX_LEVEL + 1) { lvl ->
        calculateXpForLevel(lvl)
    }

    /**
     * Calculates the exact total XP required for a specific level.
     * 
     * Internal implementation of formula:
     * XP(L) = floor(1/4 * sum_{i=1 to L-1} floor(i + 300 * 2^(i/7)))
     */
    private fun calculateXpForLevel(level: Int): Int {
        if (level <= 1) return 0
        
        var total = 0.0
        for (i in 1 until level) {
            total += floor(i + 300.0 * 2.0.pow(i / 7.0))
        }
        
        return floor(total / 4.0).toInt().coerceAtMost(MAX_XP)
    }

    /**
     * Calculates the total XP required to reach a specific level starting from level 1 (0 XP).
     *
     * @param targetLevel The target level to reach.
     * @return Total XP required for the target level.
     */
    fun totalXpForLevel(targetLevel: Int): Int {
        if (targetLevel > MAX_LEVEL) return MAX_XP
        return xpTable[targetLevel.coerceIn(1, MAX_LEVEL)]
    }

    /**
     * Calculates the skill level based on total XP.
     *
     * @param totalXp The total accumulated XP.
     * @return The current level based on total XP.
     */
    fun calculateLevelFromTotalXp(totalXp: Int): Int {
        val xp = totalXp.coerceIn(0, MAX_XP)
        
        // Binary search for efficient level lookup
        val result = xpTable.binarySearch(xp)
        return if (result >= 0) {
            result
        } else {
            val insertionPoint = -(result + 1)
            insertionPoint - 1
        }.coerceIn(1, MAX_LEVEL)
    }

    /**
     * Calculates XP required to reach the next level from current total XP.
     *
     * @param currentTotalXp The total accumulated XP.
     * @param currentLevel The current level.
     * @return XP needed to reach the next level (how much more XP is needed beyond current total).
     */
    fun xpToNextLevelFromTotal(currentTotalXp: Int, currentLevel: Int): Int {
        if (currentLevel >= MAX_LEVEL) return 0
        val totalXpForNextLevel = totalXpForLevel(currentLevel + 1)
        return (totalXpForNextLevel - currentTotalXp).coerceAtLeast(0)
    }

    /**
     * Returns comprehensive information about a skill's level and XP progress.
     */
    fun getLevelInfo(totalXp: Int): LevelInfo {
        val currentLevel = calculateLevelFromTotalXp(totalXp)
        val nextLevelXpTotal = totalXpForLevel(currentLevel + 1)
        val currentLevelXpTotal = totalXpForLevel(currentLevel)
        val xpToNextLevel = xpToNextLevelFromTotal(totalXp, currentLevel)

        val levelProgress = if (nextLevelXpTotal > currentLevelXpTotal) {
            val totalXpInLevel = nextLevelXpTotal - currentLevelXpTotal
            val xpEarnedInLevel = totalXpInLevel - xpToNextLevel
            (xpEarnedInLevel.toFloat() / totalXpInLevel).coerceIn(0f, 1f)
        } else 1f

        return LevelInfo(
            currentLevel = currentLevel,
            totalXp = totalXp,
            nextLevelXp = nextLevelXpTotal,
            xpToNextLevel = xpToNextLevel,
            progressDecimal = levelProgress
        )
    }
}
