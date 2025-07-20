package com.lucdre.idleskills.prestige.domain

/**
 * Represents the prestige points system.
 *
 * @property availablePrestigePoints Points available to spend in the skill tree
 * @property totalPrestigePoints Total points earned across all prestiges
 */
data class PrestigePoints(
    val availablePrestigePoints: Int = 0,
    val totalPrestigePoints: Int = 0
)
