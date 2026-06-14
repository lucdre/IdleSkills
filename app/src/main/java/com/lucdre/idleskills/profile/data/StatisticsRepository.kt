package com.lucdre.idleskills.profile.data

import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing player statistics.
 */
@Singleton
class StatisticsRepository @Inject constructor() : StatisticsRepositoryInterface {

    private val _statistics = MutableStateFlow(PlayerStatistics())

    override fun observeStatistics(): Flow<PlayerStatistics> {
        return _statistics.asStateFlow()
    }

    override suspend fun getStatistics(): PlayerStatistics {
        return _statistics.value
    }

    override suspend fun incrementCount(skill: SkillType, methodName: String, amount: Int) {
        _statistics.update { currentStats ->
            val updatedStatsMap = currentStats.stats.toMutableMap()
            val skillStatsMap = updatedStatsMap[skill.name]?.toMutableMap() ?: mutableMapOf()
            
            val currentCount = skillStatsMap[methodName] ?: 0
            skillStatsMap[methodName] = currentCount + amount
            
            updatedStatsMap[skill.name] = skillStatsMap
            currentStats.copy(stats = updatedStatsMap)
        }
    }
}
