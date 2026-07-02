package com.lucdre.idleskills.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.profile.domain.usecase.SetupPlayerProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling initial setup.
 *
 * @property setupPlayerProfileUseCase To set up the player profile.
 */
@HiltViewModel
class InitialSkillSelectionViewModel @Inject constructor(
    private val setupPlayerProfileUseCase: SetupPlayerProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InitialSkillSelectionUiState())
    val uiState: StateFlow<InitialSkillSelectionUiState> = _uiState.asStateFlow()

    /**
     * Attempts to set up the player profile.
     *
     * @param username The player's chosen username.
     */
    fun setupProfile(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = setupPlayerProfileUseCase(username)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isProfileSetupComplete = true
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to setup profile. Please try again."
                )
            }
        }
    }

    /**
     * Clears any existing error message.
     */
    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}

/**
 * UI state for initial skill selection screen.
 *
 * @property isLoading Whether profile setup is in progress.
 * @property isProfileSetupComplete Whether the profile has been successfully setup.
 * @property errorMessage Error message to display, if any.
 */
data class InitialSkillSelectionUiState(
    val isLoading: Boolean = false,
    val isProfileSetupComplete: Boolean = false,
    val errorMessage: String? = null,
)
