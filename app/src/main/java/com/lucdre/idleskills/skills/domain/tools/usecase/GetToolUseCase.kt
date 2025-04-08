package com.lucdre.idleskills.skills.domain.tools.usecase

import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.tools.ToolRepositoryInterface
import javax.inject.Inject

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