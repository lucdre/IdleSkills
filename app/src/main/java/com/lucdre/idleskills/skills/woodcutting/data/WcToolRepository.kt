package com.lucdre.idleskills.skills.woodcutting.data

import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.tools.ToolRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WcToolRepository @Inject constructor(): ToolRepositoryInterface {

    private val tools = mapOf(
        "Woodcutting" to listOf(
            Tool("Woodcutting", "Bronze Axe", 1.0f, 1, R.drawable.ic_tree),
            Tool("Woodcutting", "Iron Axe", 1.2f, 5, R.drawable.ic_tree),
            Tool("Woodcutting", "Steel Axe", 1.4f, 10, R.drawable.ic_tree),
            Tool("Woodcutting", "Mithril Axe", 1.6f, 20, R.drawable.ic_tree)
        )
    )

    override fun getToolsForSkill(skillName: String): List<Tool> {
        return tools[skillName] ?: emptyList()
    }
}