package com.lucdre.idleskills.prestige.domain

/**
 * Configuration for prestige system defining requirements and unlocks for each level.
 *
 * Prestige 0->1: Level 99 in WC, Mining and Fishing
 * Prestige 1->2: Placeholder
 */
object PrestigeConfig {

    /**
     * Configuration for a specific prestige level.
     *
     * @property visibleSkills Skills that should be visible at this prestige level
     * @property requiredSkills Skills that must reach the required level to advance
     * @property requiredLevel The level required for advancing
     */
    data class PrestigeLevelConfig(
        val visibleSkills: List<String>,
        val requiredSkills: List<String> = visibleSkills,
        val requiredLevel: Int = 99
    )

    private val prestigeConfigs = mapOf(
        0 to PrestigeLevelConfig(
            visibleSkills = listOf("Woodcutting", "Mining", "Fishing"),
        ),
        1 to PrestigeLevelConfig(
            visibleSkills = listOf("Woodcutting", "Mining", "Fishing", "Firemaking", "Smelting", "Cooking"),
        )
    )
    // Highest prestige level config as fallback
    private val maxPrestigeConfig = prestigeConfigs.values.maxBy { it.visibleSkills.size }

    /**
     * Get all skills that should be visible at a given prestige level.
     */
    fun getVisibleSkills(prestigeLevel: Int): List<String> {
        return prestigeConfigs[prestigeLevel]?.visibleSkills ?: maxPrestigeConfig.visibleSkills
    }

    /**
     * Get skills required to advance from the given prestige level.
     */
    fun getRequiredSkills(prestigeLevel: Int): List<String> {
        return prestigeConfigs[prestigeLevel]?.requiredSkills ?: emptyList()
    }

    /**
     * Get the level required for skills to advance from the given prestige level.
     */
    fun getRequiredLevel(prestigeLevel: Int): Int {
        return prestigeConfigs[prestigeLevel]?.requiredLevel ?: 99
    }
}