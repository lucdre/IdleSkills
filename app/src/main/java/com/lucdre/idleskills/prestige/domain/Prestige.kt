package com.lucdre.idleskills.prestige.domain

import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeProgress

/**
 * Represents the prestige system.
 *
 * @property points Available and total prestige points
 * @property skillTreeProgress Progress in the skill tree
 * @property canPrestige Whether the player can prestige (has a skill at level 99)
 */
data class Prestige(
    val points: PrestigePoints = PrestigePoints(),
    val skillTreeProgress: SkillTreeProgress = SkillTreeProgress(),
    val canPrestige: Boolean = false
)
