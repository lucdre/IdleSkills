package com.lucdre.idleskills.skills.domain.skill.usecase

import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Use case for resetting skills to their initial state.
 *
 * @property skillRepository The repository for skills.
 */
class ResetSkillsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface
) {
    /**
     * Resets all skills in the repository.
     *
     * @return The list of reset skills.
     */
    suspend operator fun invoke(): List<Skill> {
        val currentSkills = skillRepository.getSkills()
        val resetSkills = currentSkills.map { it.copy(level = 1, xp = 0) }
        return skillRepository.resetSkills(resetSkills)
    }

    /**
     * Resets the provided list of skills.
     *
     * @param skills The list of skills to reset.
     * @return The reset list of skills.
     */
    suspend operator fun invoke(skills: List<Skill>): List<Skill> {
        val resetSkills = skills.map { it.copy(level = 1, xp = 0) }
        return skillRepository.resetSkills(resetSkills)
    }
}
