package com.lucdre.idleskills.loot.presentation

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.usecase.CollectLootBoxUseCase
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootBoxCountUseCase
import com.lucdre.idleskills.loot.domain.usecase.OpenLootBoxUseCase
import com.lucdre.idleskills.skills.domain.training.usecase.GetActiveTrainingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * UI State for the Live screen.
 *
 * @property lootBoxes List of all owned loot box types.
 * @property spritePosition Position of the tappable sprite (normalized 0-1).
 * @property isSpriteVisible Whether the sprite is currently visible.
 * @property isScreenVisible Whether the Live screen is currently visible to the player.
 * @property isInventoryVisible Whether the loot box inventory bottom sheet is visible.
 * @property activeTrainingSkill Name of the skill currently being trained, if any.
 * @property activeTrainingMethod Name of the specific training method active, if any.
 * @property lastRewards Rewards from the last opened loot box.
 */
data class LiveScreenUiState(
    val lootBoxes: List<LootBox> = emptyList(),
    val spritePosition: Offset = Offset(0.5f, 0.5f),
    val isSpriteVisible: Boolean = false,
    val isScreenVisible: Boolean = false,
    val isInventoryVisible: Boolean = false,
    val activeTrainingSkill: String? = null,
    val activeTrainingMethod: String? = null,
    val lastRewards: Map<CardType, Int>? = null
)

/**
 * ViewModel for the Live screen.
 *
 * Manages the timer for spawning sprites and handles loot box collection/opening.
 */
@HiltViewModel
class LiveScreenViewModel @Inject constructor(
    private val observeLootBoxCountUseCase: ObserveLootBoxCountUseCase,
    private val collectLootBoxUseCase: CollectLootBoxUseCase,
    private val openLootBoxUseCase: OpenLootBoxUseCase,
    private val getActiveTrainingUseCase: GetActiveTrainingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveScreenUiState())
    val uiState: StateFlow<LiveScreenUiState> = _uiState.asStateFlow()

    private var spawnJob: Job? = null

    init {
        observeLootBoxes()
        observeActiveTraining()
        startSpawnTimer()
    }

    private fun observeLootBoxes() {
        viewModelScope.launch {
            observeLootBoxCountUseCase().collect { boxes ->
                _uiState.update { it.copy(lootBoxes = boxes) }
            }
        }
    }

    private fun observeActiveTraining() {
        viewModelScope.launch {
            getActiveTrainingUseCase().collect { state ->
                _uiState.update { it.copy(
                    activeTrainingSkill = state?.skillName,
                    activeTrainingMethod = state?.methodName
                ) }
            }
        }
    }

    private fun startSpawnTimer() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (true) {
                // Wait for random interval
                val delayMs = Random.nextLong(5000, 20000)
                delay(delayMs)

                // Only spawn if screen is visible AND something is being trained AND no sprite currently exists
                val state = _uiState.value
                if (state.isScreenVisible && state.activeTrainingSkill != null && !state.isSpriteVisible) {
                    // Spawn sprite near the center (0.35 to 0.65)
                    val randomX = 0.35f + Random.nextFloat() * 0.3f
                    val randomY = 0.35f + Random.nextFloat() * 0.3f
                    
                    _uiState.update { it.copy(
                        spritePosition = Offset(randomX, randomY),
                        isSpriteVisible = true
                    ) }

                    // Sprite duration
                    delay(5000)
                    _uiState.update { it.copy(isSpriteVisible = false) }
                }
            }
        }
    }

    /**
     * Updates the visibility of the screen.
     */
    fun setScreenVisible(visible: Boolean) {
        _uiState.update { it.copy(isScreenVisible = visible) }
    }

    /**
     * Toggles the visibility of the inventory bottom sheet.
     */
    fun toggleInventory(visible: Boolean) {
        _uiState.update { it.copy(isInventoryVisible = visible) }
    }

    /**
     * Handles the sprite being tapped.
     */
    fun onSpriteClick() {
        val skillName = _uiState.value.activeTrainingSkill ?: return
        if (!_uiState.value.isSpriteVisible) return

        viewModelScope.launch {
            collectLootBoxUseCase(skillName)
            _uiState.update { it.copy(isSpriteVisible = false) }
        }
    }

    /**
     * Handles the "Open Box" button click.
     */
    fun onOpenBoxClick(skillName: String) {
        viewModelScope.launch {
            openLootBoxUseCase(skillName).onSuccess { rewards ->
                _uiState.update { it.copy(lastRewards = rewards) }
            }
        }
    }

    /**
     * Clears the displayed rewards.
     */
    fun clearRewards() {
        _uiState.update { it.copy(lastRewards = null) }
    }

    override fun onCleared() {
        super.onCleared()
        spawnJob?.cancel()
    }
}
