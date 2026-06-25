package com.lucdre.idleskills.region.data

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.region.domain.RegionConfig
import com.lucdre.idleskills.region.domain.RegionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Region config repo.
 */
@Singleton
class RegionRepository @Inject constructor() : RegionRepositoryInterface {
    override fun getSkillsForRegion(region: Region): List<SkillType> {
        return RegionConfig.getSkillsForRegion(region)
    }
}
