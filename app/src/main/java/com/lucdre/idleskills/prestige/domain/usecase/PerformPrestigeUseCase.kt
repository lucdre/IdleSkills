package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigePoints
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.usecase.ResetSkillsUseCase
import javax.inject.Inject

/**
 * Use case for performing a prestige operation.
 *
 * - Awards 1 prestige point for each skill at level 99
 * - Resets all skills to level 1 with 0 XP
 * - Maintains skill tree progress and unlocked upgrades
 *
 * @property prestigeRepository The repository for prestige data.
 * @property skillRepository The repository for skill data.
 * @property getPrestigeStateUseCase Use case to see the complete state of the prestige.
 * @property resetSkillsUseCase Use case to reset all skills to initial state.
 */
class PerformPrestigeUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
    private val skillRepository: SkillRepositoryInterface,
    private val getPrestigeStateUseCase: GetPrestigeStateUseCase,
    private val resetSkillsUseCase: ResetSkillsUseCase
) {
    /**
     * Performs the prestige operation if requirements are met.
     *
     * Awards prestige points based on skills at level 99, resets all skills,
     * and maintains skill tree progress for persistent upgrades.
     *
     * @param resetTrainingState Resets all the progress of the skills.
     *
     * @return True if prestige was successful, false if requirements weren't met.
     */
    suspend operator fun invoke(resetTrainingState: () -> Unit = {}): Boolean {
        val prestigeState = getPrestigeStateUseCase()

        if (!prestigeState.canPrestige) return false

        // Calculate prestige points to award (1 per skill at level 99)
        val pointsToAward = countSkillsAtMaxLevel()

        // Reset all training state to fresh start
        resetTrainingState()

        // Reset ALL skills to level 1, 0 XP
        resetSkillsUseCase()

        // Award prestige points and update state
        val newPoints = PrestigePoints(
            availablePrestigePoints = prestigeState.points.availablePrestigePoints + pointsToAward,
            totalPrestigePoints = prestigeState.points.totalPrestigePoints + pointsToAward
        )

        prestigeRepository.updatePrestige(
            prestigeState.copy(
                points = newPoints,
                canPrestige = false // Reset until another skill reaches 99
            )
        )

        return true
    }

    /**
     * Count how many skills are at level 99.
     */
    private suspend fun countSkillsAtMaxLevel(): Int {
        val skills = skillRepository.getSkills()
        return skills.count { it.level >= 99 }
    }
}
