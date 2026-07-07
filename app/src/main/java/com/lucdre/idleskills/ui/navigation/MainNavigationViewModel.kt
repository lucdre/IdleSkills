package com.lucdre.idleskills.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootBoxCountUseCase
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.profile.domain.usecase.IsGameFreshUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing main navigation state.
 *
 * @property isGameFreshUseCase To check if the game is fresh.
 * @property profileRepository The repository for player profile data to observe reset.
 * @property observeLootBoxCountUseCase To be able to show badges when there is a new lootbox.
 *
 */
@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val isGameFreshUseCase: IsGameFreshUseCase,
    private val profileRepository: ProfileRepositoryInterface,
    private val observeLootBoxCountUseCase: ObserveLootBoxCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainNavigationUiState())
    val uiState: StateFlow<MainNavigationUiState> = _uiState.asStateFlow()

    init {
        checkGameFreshState()
        observeProfile()
        observeLootBoxes()
    }

    /**
     * Checks if the game is fresh and updates the UI state accordingly.
     */
    private fun checkGameFreshState() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isGameFresh = isGameFreshUseCase()

            _uiState.update { it.copy(
                isLoading = false,
                isGameFresh = isGameFresh
            ) }
        }
    }

    /**
     * Observes the profile to detect data resets (username becoming empty).
     */
    private fun observeProfile() {
        viewModelScope.launch {
            profileRepository.observeProfile().collect { profile ->
                if (!profile.hasCompletedSetup) {
                    _uiState.update { it.copy(isGameFresh = true) }
                }
            }
        }
    }

    /**
     * Observes the loot boxes to update the navigation badge.
     */
    private fun observeLootBoxes() {
        viewModelScope.launch {
            observeLootBoxCountUseCase()
                .distinctUntilChanged()
                .collect { lootBoxes ->
                    val hasLoot = lootBoxes.any { it.count > 0 }
                    _uiState.update { it.copy(hasLootBoxes = hasLoot) }
                }
        }
    }

    /**
     * Call when initial skill has been selected to proceed to main app.
     */
    fun onInitialSkillSelected() {
        _uiState.update { it.copy(isGameFresh = false) }
    }
}

/**
 * UI state for main navigation.
 *
 * @property isLoading Whether the navigation state is being determined.
 * @property isGameFresh Whether this is a fresh game requiring initial skill selection.
 * @property hasLootBoxes Whether there are loot boxes available to open.
 */
data class MainNavigationUiState(
    val isLoading: Boolean = true,
    val isGameFresh: Boolean = false,
    val hasLootBoxes: Boolean = false
)
