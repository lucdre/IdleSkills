package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeConfig
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeNode
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeNodeType
import javax.inject.Inject

/**
 * Use case for getting skill tree nodes that can be purchased.
 *
 * @property prestigeRepository The repository for prestige data.
 */
class GetAvailableSkillTreeNodesUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
) {
    /**
     * Gets all skill tree nodes with their availability status.
     *
     * @return Map of node ID to availability info (node, canPurchase, isUnlocked).
     */
    suspend operator fun invoke(): Map<String, NodeAvailability> {
        val prestige = prestigeRepository.getPrestige()
        val progress = prestige.skillTreeProgress
        val availablePoints = prestige.points.availablePrestigePoints

        return SkillTreeConfig.allNodes.associate { node ->
            val isUnlocked = progress.isNodeUnlocked(node.id) ||
                (
                    node.type is SkillTreeNodeType.SkillUnlock &&
                        progress.isInitialSkill(node.type.skillName)
                    )

            val canUnlock = progress.canUnlockNode(node.id)
            val canPurchase = !isUnlocked && canUnlock && (availablePoints >= node.cost)

            node.id to NodeAvailability(
                node = node,
                canPurchase = canPurchase,
                isUnlocked = isUnlocked,
                hasPrerequisites = canUnlock
            )
        }
    }

    /**
     * Data class representing the availability status of a skill tree node.
     */
    data class NodeAvailability(
        val node: SkillTreeNode,
        val canPurchase: Boolean,
        val isUnlocked: Boolean,
        val hasPrerequisites: Boolean,
    )
}
