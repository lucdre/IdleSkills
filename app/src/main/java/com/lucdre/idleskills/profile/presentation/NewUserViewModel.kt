package com.lucdre.idleskills.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.profile.domain.usecase.SetupPlayerProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling initial setup.
 *
 * @property setupPlayerProfileUseCase To set up the player profile.
 */
@HiltViewModel
class NewUserViewModel @Inject constructor(
    private val setupPlayerProfileUseCase: SetupPlayerProfileUseCase,
) : ViewModel() {

    /**
     * One-time effects for the UI.
     */
    sealed class Effect {
        data object NavigateToMain : Effect()
    }

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val _uiState = MutableStateFlow(NewUserUiState())
    val uiState: StateFlow<NewUserUiState> = _uiState.asStateFlow()

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
                _uiState.value = _uiState.value.copy(isLoading = false)
                _effect.send(Effect.NavigateToMain)
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

    /**
     * Updates the username state and clears any existing errors.
     *
     * @param newUsername The new username input.
     */
    fun updateUsername(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername, errorMessage = null)
    }
}

/**
 * UI state for initial skill selection screen.
 *
 * @property isLoading Whether profile setup is in progress.
 * @property errorMessage Error message to display, if any.
 */
data class NewUserUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String = "",
) {
    val isStartEnabled: Boolean
        get() = !isLoading && username.isNotBlank()
}
