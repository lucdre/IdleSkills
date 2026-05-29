package com.lucdre.idleskills.skills.domain.skill.usecase

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
     * @param skillName The name of the skill to be updated.
     */
    suspend operator fun invoke(skillName: String) {
        invoke(skillName, 1)
    }

    /**
     * Adds a specified amount of XP to a skill.
     * Enforces atomicity and XP capping via the repository.
     *
     * @param skillName The name of the skill to be updated.
     * @param xpAmount The amount of XP to add to the skill.
     */
    suspend operator fun invoke(skillName: String, xpAmount: Int) {
        skillRepository.addXp(skillName, xpAmount)
    }
}
