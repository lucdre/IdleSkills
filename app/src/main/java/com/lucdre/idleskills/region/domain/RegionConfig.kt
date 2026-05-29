package com.lucdre.idleskills.region.domain

import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Configuration for the region-based skill system.
 */
object RegionConfig {

    private val regionSkills = mapOf(
        Region.FIRST_REGION to listOf(SkillType.WOODCUTTING, SkillType.MINING, SkillType.FISHING),
        Region.SECOND_REGION to listOf(), // Placeholder
        Region.THIRD_REGION to listOf() // Placeholder
    )

    /**
     * Get all skills that should be visible in a given region.
     */
    fun getSkillsForRegion(region: Region): List<SkillType> {
        return regionSkills[region] ?: emptyList()
    }
}
