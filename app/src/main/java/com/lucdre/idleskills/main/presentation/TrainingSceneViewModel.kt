package com.lucdre.idleskills.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.loot.domain.LootEvent
import com.lucdre.idleskills.loot.domain.usecase.CollectLootBoxUseCase
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootEventsUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing visual elements of the training scene.
 *
 * @property collectLootBoxUseCase Use case to handle loot collection when the sprite is clicked.
 * @property observeLootEventsUseCase Use case to observe the stream of loot spawn/hide events.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingSceneViewModel @Inject constructor(
    private val collectLootBoxUseCase: CollectLootBoxUseCase,
    private val observeLootEventsUseCase: ObserveLootEventsUseCase
) : ViewModel() {

    private val _isSpawningActive = MutableStateFlow(false)
    private val _clickSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * A [StateFlow] representing the current [TrainingSceneState].
     *
     * State from [observeLootEventsUseCase] only when spawning is active.
     */
    val uiState: StateFlow<TrainingSceneState> = _isSpawningActive.flatMapLatest { active ->
        if (active) {
            observeLootEventsUseCase(_clickSignal).map { event ->
                when (event) {
                    is LootEvent.Spawn -> TrainingSceneState(
                        isSpriteVisible = true,
                        spritePosition = event.position
                    )
                    is LootEvent.Hide -> TrainingSceneState(isSpriteVisible = false)
                }
            }
        } else {
            flowOf(TrainingSceneState())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrainingSceneState()
    )

    /**
     * Updates the spawning activity based on screen visibility and training status.
     *
     * @param visible Whether the training screen is currently visible.
     * @param isTraining Whether the player is currently actively training a skill.
     */
    fun updateSpawningStatus(visible: Boolean, isTraining: Boolean) {
        _isSpawningActive.value = visible && isTraining
    }

    /**
     * Handles the collection of a loot box when the sprite is clicked.
     *
     * @param activeSkill The type of the skill currently being trained.
     */
    fun onSpriteClick(activeSkill: SkillType) {
        if (!uiState.value.isSpriteVisible) return
        
        viewModelScope.launch {
            // Signal to hide the sprite
            _clickSignal.emit(Unit)
            
            // Collect the reward
            collectLootBoxUseCase(activeSkill)
        }
    }
}
