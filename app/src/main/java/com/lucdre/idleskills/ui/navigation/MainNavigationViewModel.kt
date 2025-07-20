package com.lucdre.idleskills.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.prestige.domain.usecase.IsGameFreshUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing main navigation state.
 *
 * @property isGameFreshUseCase Use case for checking if the game is fresh.
 */
@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val isGameFreshUseCase: IsGameFreshUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainNavigationUiState())
    val uiState: StateFlow<MainNavigationUiState> = _uiState.asStateFlow()

    init {
        checkGameFreshState()
    }

    /**
     * Checks if the game is fresh and updates the UI state accordingly.
     */
    private fun checkGameFreshState() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val isGameFresh = isGameFreshUseCase()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isGameFresh = isGameFresh
            )
        }
    }

    /**
     * Call when initial skill has been selected to proceed to main app.
     */
    fun onInitialSkillSelected() {
        _uiState.value = _uiState.value.copy(isGameFresh = false)
    }
}

/**
 * UI state for main navigation.
 *
 * @property isLoading Whether the navigation state is being determined.
 * @property isGameFresh Whether this is a fresh game requiring initial skill selection.
 */
data class MainNavigationUiState(
    val isLoading: Boolean = true,
    val isGameFresh: Boolean = false,
)
