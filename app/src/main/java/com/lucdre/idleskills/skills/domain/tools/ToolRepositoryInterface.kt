package com.lucdre.idleskills.skills.domain.tools

interface ToolRepositoryInterface {
    fun getToolsForSkill(skillName: String): List<Tool>
}