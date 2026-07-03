package com.lucdre.idleskills.region.domain.usecase

import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.region.domain.RegionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Use case for retrieving skills that should be visible to the player.
 *
 * Visible skills are determined by the current region the player is in.
 *
 * @property skillRepository The repository for skill data.
 * @property sessionRepository The repository for session data.
 * @property regionRepository The repository for region data.
 */
class GetVisibleSkillsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val sessionRepository: SessionRepositoryInterface,
    private val regionRepository: RegionRepositoryInterface
) {
    /**
     * Returns the list of skills that should be visible based on the current region.
     *
     * @return A filtered list of skills available in the region.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val currentRegion = sessionRepository.getCurrentRegion()
        val regionSkills = regionRepository.getSkillsForRegion(currentRegion).toSet()

        return skills.filter { it.type in regionSkills }
    }

    /**
     * Observes the list of skills that should be visible based on current region.
     *
     * @return A Flow of filtered skills that updates when skills or player region changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeVisibleSkills(): Flow<List<Skill>> {
        return sessionRepository.observeCurrentRegion()
            .distinctUntilChanged()
            .flatMapLatest { currentRegion ->
                val regionSkills = regionRepository.getSkillsForRegion(currentRegion).toSet()
                
                skillRepository.observeSkills().map { skills ->
                    skills.filter { it.type in regionSkills }
                }
            }
    }
}
