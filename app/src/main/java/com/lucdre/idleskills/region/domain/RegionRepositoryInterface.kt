package com.lucdre.idleskills.region.domain

import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Interface for managing region-based configuration.
 */
interface RegionRepositoryInterface {
    /**
     * Returns the list of skill types available in a given region.
     */
    fun getSkillsForRegion(region: Region): List<SkillType>
}
