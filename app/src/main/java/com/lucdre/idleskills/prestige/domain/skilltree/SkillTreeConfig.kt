package com.lucdre.idleskills.prestige.domain.skilltree

/**
 * Configuration for the prestige skill tree defining all available upgrades.
 */
object SkillTreeConfig {

    /**
     * All available skill tree nodes.
     */
    val allNodes = listOf(
        // First tier - Additional base skills
        SkillTreeNode(
            id = "unlock_mining",
            name = "Mining",
            description = "Unlock the Mining skill",
            cost = 1,
            type = SkillTreeNodeType.SkillUnlock("Mining")
        ),
        SkillTreeNode(
            id = "unlock_fishing",
            name = "Fishing",
            description = "Unlock the Fishing skill",
            cost = 1,
            type = SkillTreeNodeType.SkillUnlock("Fishing")
        ),
        SkillTreeNode(
            id = "unlock_woodcutting",
            name = "Woodcutting",
            description = "Unlock the Woodcutting skill",
            cost = 1,
            type = SkillTreeNodeType.SkillUnlock("Woodcutting")
        ),

        // Second tier - Processing skills
        SkillTreeNode(
            id = "unlock_smelting",
            name = "Smelting",
            description = "Unlock the Smelting skill",
            cost = 2,
            prerequisites = listOf("unlock_woodcutting", "unlock_mining"),
            type = SkillTreeNodeType.SkillUnlock("Smelting")
        ),
        SkillTreeNode(
            id = "unlock_cooking",
            name = "Cooking",
            description = "Unlock the Cooking skill",
            cost = 2,
            prerequisites = listOf("unlock_fishing"),
            type = SkillTreeNodeType.SkillUnlock("Cooking")
        ),
        SkillTreeNode(
            id = "unlock_smithing",
            name = "Smithing",
            description = "Unlock the Smithing skill",
            cost = 5,
            prerequisites = listOf("unlock_smelting"),
            type = SkillTreeNodeType.SkillUnlock("Smithing")
        ),
        // Global bonuses
        SkillTreeNode(
            id = "global_exp_bonus_1",
            name = "[PH]Efficient Learning I",
            description = "[PH]Gain 10% more experience",
            cost = 3,
            type = SkillTreeNodeType.ExperienceBonus(1.1)
        ),
        SkillTreeNode(
            id = "global_speed_bonus_1",
            name = "[PH]Swift Actions I",
            description = "[PH]All actions are 10% faster",
            cost = 3,
            type = SkillTreeNodeType.SpeedBonus(0.9)
        )
    )

    /**
     * Get a skill tree node by its ID.
     */
    fun getNode(nodeId: String): SkillTreeNode? {
        return allNodes.find { it.id == nodeId }
    }

    // TODO these 2 for tooltips?
    /**
     * Get all nodes that are prerequisites for the given node.
     */
    fun getPrerequisiteNodes(nodeId: String): List<SkillTreeNode> {
        val node = getNode(nodeId) ?: return emptyList()
        return node.prerequisites.mapNotNull { getNode(it) }
    }

    /**
     * Get all nodes that require the given node as a prerequisite.
     */
    fun getDependentNodes(nodeId: String): List<SkillTreeNode> {
        return allNodes.filter { it.prerequisites.contains(nodeId) }
    }
}
