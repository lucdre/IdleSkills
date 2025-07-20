package com.lucdre.idleskills.prestige.domain.skilltree

/**
 * Represents the player's progress in the prestige skill tree.
 *
 * @property unlockedNodes Set of node IDs that have been unlocked
 * @property selectedInitialSkill The skill chosen at game start (null if not selected yet)
 */
data class SkillTreeProgress(
    val unlockedNodes: Set<String> = emptySet(),
    val selectedInitialSkill: String? = null
) {

    /**
     * Helper to check if a node is "fulfilled": unlocked or covered as initial skill
     */
    private fun isNodeFulfilled(nodeId: String): Boolean {
        if (unlockedNodes.contains(nodeId)) return true
        val node = SkillTreeConfig.getNode(nodeId)
        return node is SkillTreeNode && node.type is SkillTreeNodeType.SkillUnlock &&
            node.type.skillName == selectedInitialSkill
    }

    /**
     * Check if a specific node is unlocked or fulfilled.
     */
    fun isNodeUnlocked(nodeId: String): Boolean {
        return isNodeFulfilled(nodeId)
    }

    /**
     * Check if a node can be unlocked.
     */
    fun canUnlockNode(nodeId: String): Boolean {
        val node = SkillTreeConfig.getNode(nodeId) ?: return false
        return node.prerequisites.all { prerequisite ->
            isNodeFulfilled(prerequisite)
        }
    }

    /**
     * Get all unlocked skills based on current progress.
     */
    fun getUnlockedSkills(): List<String> {
        val skills = mutableListOf<String>()

        // Add initial skill if selected
        selectedInitialSkill?.let { skills.add(it) }

        // Add skills from unlocked nodes
        unlockedNodes.forEach { nodeId ->
            val node = SkillTreeConfig.getNode(nodeId)
            if (node?.type is SkillTreeNodeType.SkillUnlock) {
                skills.add(node.type.skillName)
            }
        }

        return skills.distinct()
    }

    /**
     * Check if a skill is the initially selected skill (shouldn't appear in skill tree).
     */
    fun isInitialSkill(skillName: String): Boolean {
        return selectedInitialSkill == skillName
    }
}
