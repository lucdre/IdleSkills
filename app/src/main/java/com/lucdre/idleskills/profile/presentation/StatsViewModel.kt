package com.lucdre.idleskills.profile.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.profile.domain.usecase.ObserveStatisticsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * UI state for the statistics screen.
 *
 * @property skills The list of all player skills and their current progress.
 * @property statistics Cumulative player statistics across all skills.
 * @property isLoading Whether the statistics data is currently being loaded.
 */
@Immutable
data class StatsUiState(
    val skills: List<Skill> = emptyList(),
    val statistics: PlayerStatistics = PlayerStatistics(),
    val isLoading: Boolean = true
)

/**
 * ViewModel responsible for managing and providing player statistics and skill progress data.
 *
 * @property observeStatisticsUseCase Use case to observe cumulative player statistics.
 * @property skillRepository Repository to observe the current state of all player skills.
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val observeStatisticsUseCase: ObserveStatisticsUseCase,
    private val skillRepository: SkillRepositoryInterface
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        skillRepository.observeSkills(),
        observeStatisticsUseCase.observeStatistics()
    ) { skills, stats ->
        StatsUiState(
            skills = skills,
            statistics = stats,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )
}
