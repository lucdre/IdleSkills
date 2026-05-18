package com.lucdre.idleskills.region.domain

/**
 * Configuration for the region-based skill system.
 */
object RegionConfig {

    private val regionSkills = mapOf(
        Region.FIRST_REGION to listOf("Woodcutting", "Mining", "Fishing"),
        Region.SECOND_REGION to listOf(), // Placeholder
        Region.THIRD_REGION to listOf() // Placeholder
    )

    /**
     * Get all skills that should be visible in a given region.
     */
    fun getSkillsForRegion(region: Region): List<String> {
        return regionSkills[region] ?: emptyList()
    }
}
