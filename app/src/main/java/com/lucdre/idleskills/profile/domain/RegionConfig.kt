package com.lucdre.idleskills.profile.domain

/**
 * Configuration for the region-based skill system.
 */
object RegionConfig {

    private val regionSkills = mapOf(
        "First Region" to listOf("Woodcutting", "Mining", "Fishing"),
        "Second Region" to listOf() // Placeholder
    )

    /**
     * Get all skills that should be visible in a given region.
     */
    fun getSkillsForRegion(regionName: String): List<String> {
        return regionSkills[regionName] ?: emptyList()
    }
}
