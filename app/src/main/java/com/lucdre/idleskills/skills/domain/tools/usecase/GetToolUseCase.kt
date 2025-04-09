package com.lucdre.idleskills.skills.domain.tools.usecase

import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.tools.ToolRepositoryInterface

/**
 * Use case for retrieving tools for training a specific skill.
 *
 * @property toolRepository The repository for tools.
 *
 * Methods:
 * - [invoke]: Get all available tools for a specific skill.
 * - [getBestAvailableTool]: Returns the best available tool for a skill at the current level. (Placeholder)
 */
class GetToolUseCase (
    private val toolRepository: ToolRepositoryInterface
) {
    operator fun invoke(skillName: String): List<Tool> {
        return toolRepository.getToolsForSkill(skillName)
    }

    fun getBestAvailableTool(skillName: String, currentLevel: Int): Tool? {
        val tools = toolRepository.getToolsForSkill(skillName)
        val availableTools = tools.filter { it.requiredLevel <= currentLevel }
        return availableTools.maxByOrNull { it.requiredLevel }
    }
}