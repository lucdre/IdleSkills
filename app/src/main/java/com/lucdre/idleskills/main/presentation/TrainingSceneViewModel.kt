package com.lucdre.idleskills.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.loot.domain.LootEvent
import com.lucdre.idleskills.loot.domain.usecase.CollectLootRewardsUseCase
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootEventsUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One-time UI effects for the training scene.
 */
sealed class TrainingSceneUiEffect {
    data class ShowLootMessage(val message: String) : TrainingSceneUiEffect()
}

/**
 * ViewModel responsible for managing visual elements of the training scene.
 *
 * @property collectLootRewardsUseCase Use case to handle loot collection when the sprite is clicked.
 * @property observeLootEventsUseCase Use case to observe the stream of loot spawn/hide events.
 * @property itemRegistry For looking up item metadata for messages.
 * @property trainingService Service providing the global training state to synchronize sprite spawns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingSceneViewModel @Inject constructor(
    private val collectLootRewardsUseCase: CollectLootRewardsUseCase,
    private val observeLootEventsUseCase: ObserveLootEventsUseCase,
    private val itemRegistry: ItemRegistry,
    private val trainingService: TrainingService
) : ViewModel() {

    private val _isScreenVisible = MutableStateFlow(false)
    private val _clickSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * A [StateFlow] representing the current [TrainingSceneState].
     */
    val uiState: StateFlow<TrainingSceneState> = combine(
        _isScreenVisible,
        trainingService.trainingState.map { it.activeSkillName != null }.distinctUntilChanged()
    ) { isVisible, isTraining ->
        isVisible && isTraining
    }.flatMapLatest { active ->
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

    private val _uiEffects = MutableSharedFlow<TrainingSceneUiEffect>()
    val uiEffects: SharedFlow<TrainingSceneUiEffect> = _uiEffects.asSharedFlow()

    /**
     * Updates the spawning activity based on screen visibility.
     *
     * @param visible Whether the training screen is currently visible.
     */
    fun setScreenVisible(visible: Boolean) {
        _isScreenVisible.value = visible
    }

    /**
     * Handles the collection of rewards when the sprite is clicked.
     */
    fun onSpriteClick() {
        if (!uiState.value.isSpriteVisible) return
        
        viewModelScope.launch {
            // Signal to hide the sprite
            _clickSignal.emit(Unit)
            
            // Collect the rewards
            val reward = collectLootRewardsUseCase()
            
            if (reward != null) {
                val message = buildString {
                    val items = reward.items.entries.joinToString(", ") { (type, qty) ->
                        val metadata = itemRegistry.getMetadata(type)
                        "$qty ${metadata.displayName}"
                    }
                    if (items.isNotEmpty()) {
                        append("Found $items")
                    }
                    
                    reward.droppedBox?.let { skill ->
                        if (isNotEmpty()) append(" and a ")
                        else append("Found a ")
                        
                        val boxName = when (skill) {
                            SkillType.WOODCUTTING -> "Bird's Nest"
                            SkillType.MINING -> "Geode"
                            SkillType.FISHING -> "Treasure Chest"
                        }
                        append(boxName)
                    }
                    append("!")
                }
                _uiEffects.emit(TrainingSceneUiEffect.ShowLootMessage(message))
            }
        }
    }
}
