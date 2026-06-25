package com.lucdre.idleskills.skills.domain.skill

import kotlinx.coroutines.flow.Flow

/**
 * Skill repository interface.
 */
interface SkillRepositoryInterface {
    /**
     * Observe skills.
     */
    fun observeSkills(): Flow<List<Skill>>

    /**
     * @return The list of skills.
     */
    suspend fun getSkills(): List<Skill>

    /**
     * @param name The name of the skill to fetch.
     * @return The skill with the given name, or null if not found.
     */
    suspend fun getSkillByName(name: String): Skill?

    /**
     * Adds XP to a skill.
     *
     * @param skillName The name of the skill.
     * @param amount The amount of XP to add.
     */
    suspend fun addXp(skillName: String, amount: Int)

    /**
     * Resets skills in the repository.
     * 
     * @param skills The list of skills to reset.
     * @return The reset list of skills.
     */
    suspend fun resetSkills(skills: List<Skill>): List<Skill>
}
