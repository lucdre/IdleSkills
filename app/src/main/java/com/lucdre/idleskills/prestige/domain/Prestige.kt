package com.lucdre.idleskills.prestige.domain

/**
 *  Represents the prestige system.
 *
 *  @property level The current prestige level.
 *  @property canPrestige Whether the player can prestige.
 */
data class Prestige(
    val level: Int = 0,
    val canPrestige: Boolean = false
)