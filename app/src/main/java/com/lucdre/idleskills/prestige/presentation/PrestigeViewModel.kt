package com.lucdre.idleskills.prestige.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.usecase.GetPrestigeStateUseCase
import com.lucdre.idleskills.prestige.domain.usecase.PerformPrestigeUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for prestige-related ui state and operations.
 *
 * Manages prestige state, handles prestige actions, provides
 * data for prestige UI components.
 *
 * @property getPrestigeStateUseCase Use case for retrieving prestige state with requirements
 * @property performPrestigeUseCase Use case for performing prestige operations
 */
@HiltViewModel
class PrestigeViewModel @Inject constructor(
    private val getPrestigeStateUseCase: GetPrestigeStateUseCase,
    private val performPrestigeUseCase: PerformPrestigeUseCase,
    private val skillRepository: SkillRepositoryInterface
) : ViewModel() {

    // private to update the state in the viewModel
    private val _uiState = MutableStateFlow(PrestigeUiState())
    // public so that the UI can observe the state but not modify it
    val uiState: StateFlow<PrestigeUiState> = _uiState.asStateFlow()

    init {
        loadPrestigeState()

        viewModelScope.launch {
            skillRepository.observeSkills().collect { skills ->
                Log.d("PrestigeViewModel", "Skills changed, refreshing prestige state")
                loadPrestigeState()
            }
        }
    }

    /**
     * Loads the current prestige state, including whether prestige is possible.
     */
    fun loadPrestigeState() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val prestigeState = getPrestigeStateUseCase()
                _uiState.value = _uiState.value.copy(
                    prestige = prestigeState,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load prestige state: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Attempts to prestige.
     */
    fun prestige(resetTrainingState: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPerformingPrestige = true)
            try {
                val success = performPrestigeUseCase(resetTrainingState)
                if (success) {
                    // Reload prestige case
                    _uiState.value = _uiState.value.copy(isPerformingPrestige = false)
                    loadPrestigeState()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Cannot prestige: Requirements not met",
                        isPerformingPrestige = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to prestige: ${e.message}",
                    isPerformingPrestige = false
                )
            }
        }
    }
}

/**
 * UI state for prestige-related screens.
 *
 * @property prestige Current prestige state
 * @property isLoading Whether prestige data is being loaded
 * @property isPerformingPrestige Whether a prestige operation is in progress
 * @property error Error message
 */
data class PrestigeUiState(
    val prestige: Prestige = Prestige(),
    val isLoading: Boolean = false,
    val isPerformingPrestige: Boolean = false,
    val error: String? = null
)