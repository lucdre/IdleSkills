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
 * ViewModel for handling initial skill selection and profile setup.
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
        if (username.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Username cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val success = setupPlayerProfileUseCase(username)

            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isProfileSetupComplete = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to setup profile. Please try again."
                )
            }
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
