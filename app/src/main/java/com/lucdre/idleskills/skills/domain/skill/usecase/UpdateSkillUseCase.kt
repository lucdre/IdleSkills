package com.lucdre.idleskills.skills.domain.skill.usecase

import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Use case for updating skills, adding XP and leveling them up.
 *
 * @property skillRepository The repository for skills.
 */
class UpdateSkillUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface
) {
    /**
     * Adds 1 XP to a skill. Used for cases where no specified XP to add is provided.
     *
     * @param skill The skill to be updated.
     * @return The updated skill with one more XP.
     */
    suspend operator fun invoke(skill: Skill): Skill {
        return invoke(skill, 1)
    }

    /**
     * Adds a specified amount of XP to a skill.
     * Also checks if the skill can level up.
     *
     * @param skill The skill to be updated.
     * @param xpAmount The amount of XP to add to the skill.
     * @return The updated skill with the added XP.
     */
    suspend operator fun invoke(skill: Skill, xpAmount: Int): Skill {
        if ((skill.xp + xpAmount) >= LevelCalculator.maxXp) {
            val updatedSkill = skill.copy(xp = LevelCalculator.maxXp)
            return skillRepository.updateSkill(updatedSkill)
        }

        val newXp = skill.xp + xpAmount
        val newLevel = LevelCalculator.calculateLevelFromTotalXp(newXp)

        val updatedSkill = skill.copy(xp = newXp, level = newLevel)
        return skillRepository.updateSkill(updatedSkill)
    }
}
