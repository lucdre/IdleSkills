package com.lucdre.idleskills.prestige.domain.skilltree

/**
 * Represents the player's progress in the prestige skill tree.
 *
 * @property unlockedNodes Set of node IDs that have been unlocked
 */
data class SkillTreeProgress(
    val unlockedNodes: Set<String> = emptySet()
) {

    /**
     * Helper to check if a node is "fulfilled": unlocked
     */
    private fun isNodeFulfilled(nodeId: String): Boolean {
        return unlockedNodes.contains(nodeId)
    }

    /**
     * Check if a specific node is unlocked.
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
}
