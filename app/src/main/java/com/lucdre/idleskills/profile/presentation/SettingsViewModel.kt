package com.lucdre.idleskills.profile.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.core.domain.usecase.ResetAllDataUseCase
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.usecase.GetPlayerProfileUseCase
import com.lucdre.idleskills.skills.domain.training.TrainingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the settings screen.
 *
 * @property playerProfile The current player's profile information.
 * @property currentRegionName The name of the region the player is currently in.
 * @property isLoading Whether the settings data is currently being loaded.
 */
@Immutable
data class SettingsUiState(
    val playerProfile: PlayerProfile = PlayerProfile(),
    val currentRegionName: String = "",
    val isLoading: Boolean = true
)

/**
 * ViewModel responsible for managing player settings, profile data, etc.
 *
 * @property getPlayerProfileUseCase Use case to observe the player's profile data.
 * @property sessionRepository Repository to observe current session data, such as the active region.
 * @property resetAllDataUseCase Use case to reset all player progress and data.
 * @property trainingService Service used to stop any active training before data reset.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getPlayerProfileUseCase: GetPlayerProfileUseCase,
    private val sessionRepository: SessionRepositoryInterface,
    private val resetAllDataUseCase: ResetAllDataUseCase,
    private val trainingService: TrainingService
) : ViewModel() {

    sealed class Effect {
        object TriggerRebirth : Effect()
    }

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val uiState: StateFlow<SettingsUiState> = combine(
        getPlayerProfileUseCase.observeProfile(),
        sessionRepository.observeCurrentRegion()
    ) { profile, region ->
        SettingsUiState(
            playerProfile = profile,
            currentRegionName = region.displayName,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun resetAllData() {
        viewModelScope.launch {
            trainingService.stopTraining()
            resetAllDataUseCase()
            _effect.send(Effect.TriggerRebirth)
        }
    }
}
