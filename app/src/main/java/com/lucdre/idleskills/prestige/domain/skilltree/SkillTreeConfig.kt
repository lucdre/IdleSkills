package com.lucdre.idleskills.prestige.domain.skilltree

/**
 * Skill tree config.
 */
object SkillTreeConfig {

    /**
     * All available skill tree nodes.
     */
    val allNodes = listOf(
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
