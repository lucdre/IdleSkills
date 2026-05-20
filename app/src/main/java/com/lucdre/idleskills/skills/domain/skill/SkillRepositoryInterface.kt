package com.lucdre.idleskills.skills.domain.skill

import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing skill data.
 */
interface SkillRepositoryInterface {
    /**
     * @return A [Flow] of the list of skills that updates when data changes.
     */
    fun observeSkills(): Flow<List<Skill>>

    /**
     * @return The list of skills.
     */
    suspend fun getSkills(): List<Skill>

    /**
     * Updates a skill in the repository.
     *
     * @param skill The skill to be updated.
     * @return The updated skill.
     */
    suspend fun updateSkill(skill: Skill): Skill

    /**
     * Resets skills in the repository.
     * 
     * @param skills The list of skills to reset.
     * @return The reset list of skills.
     */
    suspend fun resetSkills(skills: List<Skill>): List<Skill>

    /**
     * Observes the current active training session.
     */
    fun observeActiveTraining(): Flow<ActiveTraining?>

    /**
     * Sets the current active training session.
     */
    suspend fun setActiveTraining(training: ActiveTraining?)
}
