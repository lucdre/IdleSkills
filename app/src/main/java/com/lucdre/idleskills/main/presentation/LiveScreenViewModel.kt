package com.lucdre.idleskills.main.presentation

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.usecase.CollectLootBoxUseCase
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootBoxCountUseCase
import com.lucdre.idleskills.loot.domain.usecase.OpenLootBoxUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.usecase.GetActiveTrainingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * UI State for the Live screen.
 */
data class LiveScreenUiState(
    val lootBoxes: List<LootBox> = emptyList(),
    val spritePosition: Offset = Offset(0.5f, 0.5f),
    val isSpriteVisible: Boolean = false,
    val isScreenVisible: Boolean = false,
    val isInventoryVisible: Boolean = false,
    val activeTrainingSkill: SkillType? = null,
    val activeTrainingMethod: String? = null,
    val lastRewards: Map<CardType, Int>? = null
)

/**
 * ViewModel for the Live screen.
 *
 * Manages the core visual state of the game world, including animations,
 * sprite spawning, and loot interaction.
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
        observeVisibilityAndManageTimer()
    }

    private fun observeLootBoxes() {
        observeLootBoxCountUseCase()
            .onEach { boxes -> _uiState.update { it.copy(lootBoxes = boxes) } }
            .launchIn(viewModelScope)
    }

    private fun observeActiveTraining() {
        getActiveTrainingUseCase()
            .onEach { trainingState -> 
                _uiState.update { it.copy(
                    activeTrainingSkill = trainingState?.skillName?.let { SkillType.fromString(it) },
                    activeTrainingMethod = trainingState?.methodName
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeVisibilityAndManageTimer() {
        // Monitor visibility and active training to start/stop the timer
        _uiState.map { it.isScreenVisible && it.activeTrainingSkill != null }
            .distinctUntilChanged()
            .onEach { shouldRun ->
                if (shouldRun) {
                    startSpawnTimer()
                } else {
                    stopSpawnTimer()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startSpawnTimer() {
        stopSpawnTimer()
        spawnJob = viewModelScope.launch {
            while (true) {
                // Wait for random interval
                val delayMs = Random.nextLong(5000, 20000)
                delay(delayMs)

                // Only spawn if no sprite currently exists
                if (!_uiState.value.isSpriteVisible) {
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

    private fun stopSpawnTimer() {
        spawnJob?.cancel()
        spawnJob = null
        _uiState.update { it.copy(isSpriteVisible = false) }
    }

    fun setScreenVisible(visible: Boolean) {
        _uiState.update { it.copy(isScreenVisible = visible) }
    }

    fun toggleInventory(visible: Boolean) {
        _uiState.update { it.copy(isInventoryVisible = visible) }
    }

    fun onSpriteClick() {
        val skill = _uiState.value.activeTrainingSkill ?: return
        if (!_uiState.value.isSpriteVisible) return

        viewModelScope.launch {
            collectLootBoxUseCase(skill)
            _uiState.update { it.copy(isSpriteVisible = false) }
        }
    }

    fun onOpenBoxClick(skill: SkillType) {
        viewModelScope.launch {
            openLootBoxUseCase(skill).onSuccess { rewards ->
                _uiState.update { it.copy(lastRewards = rewards) }
            }
        }
    }

    fun clearRewards() {
        _uiState.update { it.copy(lastRewards = null) }
    }
}
