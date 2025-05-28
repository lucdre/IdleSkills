package com.lucdre.idleskills.skills.domain.skill.usecase

import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface

/**
 * Use case for resetting skills from the repository.
 *
 * @property skillRepository The repository for skills.
 *
 */
class ResetSkillsUseCase(private val skillRepository: SkillRepositoryInterface) {
    /**
     * Resets all skills to Level 1 and 0 XP. Used for cases where no list of skills to reset is provided.
     *
     * @return The reset list of skills.
     */
    suspend operator fun invoke(): List<Skill> {
        val currentSkills = skillRepository.getSkills()
        val resetSkills = currentSkills.map { skill ->
            skill.copy(level = 1, xp = 0)
        }

        return skillRepository.resetSkills(resetSkills)
    }

    /**
     * Resets provided skills to Level 1 and 0 XP.
     *
     * @param skillsToReset The skills to be reset.
     * @return The reset list of skills.
     */
    suspend operator fun invoke(skillsToReset: List<Skill>): List<Skill> {
        val resetSkills = skillsToReset.map { skill ->
            skill.copy(level = 1, xp = 0)
        }

        return skillRepository.resetSkills(resetSkills)
    }
}