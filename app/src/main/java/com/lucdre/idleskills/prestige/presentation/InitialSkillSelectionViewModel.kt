package com.lucdre.idleskills.prestige.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.prestige.domain.usecase.SelectInitialSkillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling initial skill selection.
 *
 * @property selectInitialSkillUseCase Use case for selecting the initial skill.
 */
@HiltViewModel
class InitialSkillSelectionViewModel @Inject constructor(
    private val selectInitialSkillUseCase: SelectInitialSkillUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InitialSkillSelectionUiState())
    val uiState: StateFlow<InitialSkillSelectionUiState> = _uiState.asStateFlow()

    /**
     * Attempts to select an initial skill.
     *
     * @param skillName The name of the skill to select.
     */
    fun selectSkill(skillName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val success = selectInitialSkillUseCase(skillName)

            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSkillSelected = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to select skill. Please try again."
                )
            }
        }
    }
}

/**
 * UI state for initial skill selection screen.
 *
 * @property isLoading Whether a skill selection is in progress.
 * @property isSkillSelected Whether a skill has been successfully selected.
 * @property errorMessage Error message to display, if any.
 */
data class InitialSkillSelectionUiState(
    val isLoading: Boolean = false,
    val isSkillSelected: Boolean = false,
    val errorMessage: String? = null,
)
