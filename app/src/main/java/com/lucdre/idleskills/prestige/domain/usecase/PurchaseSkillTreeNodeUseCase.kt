package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigePoints
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeConfig
import javax.inject.Inject

/**
 * Use case for purchasing a skill tree node with prestige points.
 *
 * @property prestigeRepository The repository for prestige data.
 */
class PurchaseSkillTreeNodeUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
) {
    /**
     * Attempts to purchase a skill tree node.
     *
     * @param nodeId The ID of the node to purchase.
     * @return True if purchase was successful, false otherwise.
     */
    suspend operator fun invoke(nodeId: String): Boolean {
        val currentPrestige = prestigeRepository.getPrestige()
        val progress = currentPrestige.skillTreeProgress
        val node = SkillTreeConfig.getNode(nodeId) ?: return false

        // Check if node can be purchased
        if (progress.isNodeUnlocked(nodeId)) return false // Already unlocked
        if (!progress.canUnlockNode(nodeId)) return false // Prerequisites not met
        if (currentPrestige.points.availablePrestigePoints < node.cost) return false // Can't afford

        // Deduct points and unlock node
        val newPoints = PrestigePoints(
            availablePrestigePoints = currentPrestige.points.availablePrestigePoints - node.cost,
            totalPrestigePoints = currentPrestige.points.totalPrestigePoints
        )

        val newProgress = progress.copy(
            unlockedNodes = progress.unlockedNodes + nodeId
        )

        val updatedPrestige = currentPrestige.copy(
            points = newPoints,
            skillTreeProgress = newProgress
        )

        prestigeRepository.updatePrestige(updatedPrestige)
        return true
    }
}
