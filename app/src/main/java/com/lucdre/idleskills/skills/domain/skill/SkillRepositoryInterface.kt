package com.lucdre.idleskills.skills.domain.skill

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling skill-related data operations.
 */
interface SkillRepositoryInterface {
    /**
     * @return A [Flow] of skill lists that update when data changes.
     */
    fun observeSkills(): Flow<List<Skill>>

    /**
     * @return The current list of skills.
     */
    suspend fun getSkills(): List<Skill>

    /**
     * Updates a specific skill in the repository.
     *
     * @param skill The skill to be updated.
     * @return The updated skill.
     */
    suspend fun updateSkill(skill: Skill): Skill
}