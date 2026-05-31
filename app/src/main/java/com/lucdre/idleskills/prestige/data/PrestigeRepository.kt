package com.lucdre.idleskills.prestige.data

import com.lucdre.idleskills.core.persistence.PrestigeDao
import com.lucdre.idleskills.core.persistence.PrestigeEntity
import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.PrestigePoints
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prestige repository.
 */
@Singleton
class PrestigeRepository @Inject constructor(
    private val prestigeDao: PrestigeDao,
) : PrestigeRepositoryInterface {

    override fun observePrestige(): Flow<Prestige> {
        return prestigeDao.observePrestige().map { entity ->
            Prestige(
                points = PrestigePoints(
                    availablePrestigePoints = entity?.availablePrestigePoints ?: 0,
                    totalPrestigePoints = entity?.totalPrestigePoints ?: 0
                ),
                skillTreeProgress = SkillTreeProgress(
                    unlockedNodes = entity?.unlockedSkillTreeNodes ?: emptySet()
                )
            )
        }
    }

    override suspend fun getPrestige(): Prestige {
        val entity = prestigeDao.getPrestige()
        return Prestige(
            points = PrestigePoints(
                availablePrestigePoints = entity?.availablePrestigePoints ?: 0,
                totalPrestigePoints = entity?.totalPrestigePoints ?: 0
            ),
            skillTreeProgress = SkillTreeProgress(
                unlockedNodes = entity?.unlockedSkillTreeNodes ?: emptySet()
            )
        )
    }

    override suspend fun updatePrestige(prestige: Prestige) {
        val currentPrestige = prestigeDao.getPrestige() ?: PrestigeEntity()
        prestigeDao.insertOrUpdate(
            currentPrestige.copy(
                availablePrestigePoints = prestige.points.availablePrestigePoints,
                totalPrestigePoints = prestige.points.totalPrestigePoints,
                unlockedSkillTreeNodes = prestige.skillTreeProgress.unlockedNodes
            )
        )
    }
}
