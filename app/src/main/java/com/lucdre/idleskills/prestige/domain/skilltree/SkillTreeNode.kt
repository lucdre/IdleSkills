package com.lucdre.idleskills.prestige.domain.skilltree

/**
 * Represents a node in the prestige skill tree.
 *
 * @property id Unique identifier for this node
 * @property name Display name of the upgrade
 * @property description Description of the upgrade
 * @property cost Prestige points required to unlock
 * @property prerequisites List of node IDs that must be unlocked first
 * @property type The type of upgrade this node provides
 */
data class SkillTreeNode(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val prerequisites: List<String> = emptyList(),
    val type: SkillTreeNodeType
)

/**
 * Types of upgrades available in the skill tree.
 */
sealed class SkillTreeNodeType {
    /**
     * Unlocks a new skill for training.
     */
    data class SkillUnlock(val skillName: String) : SkillTreeNodeType()

    /**
     * Unlocks a new tool for a specific skill.
     */
    data class ToolUnlock(val toolId: String, val skillName: String) : SkillTreeNodeType()

    /**
     * Provides a permanent bonus to experience gain.
     */
    data class ExperienceBonus(val multiplier: Double, val skillName: String? = null) : SkillTreeNodeType()

    /**
     * Reduces training time for actions.
     */
    data class SpeedBonus(val multiplier: Double, val skillName: String? = null) : SkillTreeNodeType()
}