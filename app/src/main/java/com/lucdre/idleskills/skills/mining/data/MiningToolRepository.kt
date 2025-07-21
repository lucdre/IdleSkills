package com.lucdre.idleskills.skills.mining.data

import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.tools.ToolRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository that provides mining tools.
 */
@Singleton
class MiningToolRepository @Inject constructor() : ToolRepositoryInterface {

    private val tools = mapOf(
        "Mining" to listOf(
            Tool("Mining", "Bronze Pickaxe", 1.0f, 1, R.drawable.ic_tree),
            Tool("Mining", "Iron Pickaxe", 1.05f, 5, R.drawable.ic_tree),
            Tool("Mining", "Steel Pickaxe", 1.1f, 15, R.drawable.ic_tree),
            Tool("Mining", "Mithril Pickaxe", 1.15f, 25, R.drawable.ic_tree),
            Tool("Mining", "Adamant Pickaxe", 1.2f, 40, R.drawable.ic_tree),
            Tool("Mining", "Rune Pickaxe", 1.13f, 60, R.drawable.ic_tree),
            Tool("Mining", "Dragon Pickaxe", 1.5f, 80, R.drawable.ic_tree)
        )
    )

    /**
     * Retrieves tools available for the specified skill.
     *
     * @param skillName The name of the skill to get tools for
     * @return List of tools available for the skill, or empty list if skill not found
     */
    override fun getToolsForSkill(skillName: String): List<Tool> {
        return tools[skillName] ?: emptyList()
    }
}
