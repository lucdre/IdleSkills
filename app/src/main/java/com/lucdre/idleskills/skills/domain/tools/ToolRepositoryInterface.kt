package com.lucdre.idleskills.skills.domain.tools

/**
 * Interface for managing tool data.
 */
interface ToolRepositoryInterface {
    /**
     * Retrieves all tools available for a specific skill.
     *
     * @param skillName The name of the skill to retrieve tools for.
     * @return A list of tools available for the specified skill.
     */
    fun getToolsForSkill(skillName: String): List<Tool>
}