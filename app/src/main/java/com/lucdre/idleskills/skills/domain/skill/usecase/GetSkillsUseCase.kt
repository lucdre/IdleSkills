package com.lucdre.idleskills.skills.domain.skill.usecase

import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving skills from the repository.
 *
 * @property skillRepository The repository for skills.
 *
 * Methods:
 * - [invoke]: Returns a list of all skills.
 * - [observeSkills]: Returns a flow of skills whenever a skill is updated.
 */
class GetSkillsUseCase (private val skillRepository: SkillRepositoryInterface) {
    suspend operator fun invoke(): List<Skill> {
        return skillRepository.getSkills()
    }

    fun observeSkills(): Flow<List<Skill>> {
        return skillRepository.observeSkills()
    }
}