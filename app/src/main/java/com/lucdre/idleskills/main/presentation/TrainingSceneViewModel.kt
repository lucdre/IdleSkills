package com.lucdre.idleskills.main.presentation

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.loot.domain.usecase.CollectLootBoxUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillType
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
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel responsible for managing visual elements of the training scene,
 * such as the random spawning of loot sprites.
 *
 * @property collectLootBoxUseCase Use case to handle loot collection when the sprite is clicked.
 */
@HiltViewModel
class TrainingSceneViewModel @Inject constructor(
    private val collectLootBoxUseCase: CollectLootBoxUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingSceneState())
    val uiState: StateFlow<TrainingSceneState> = _uiState.asStateFlow()

    private var spawnJob: Job? = null

    /**
     * Updates the spawning timer based on screen visibility and training status.
     *
     * @param visible Whether the training screen is currently visible.
     * @param isTraining Whether the player is currently actively training a skill.
     */
    fun updateSpawningStatus(visible: Boolean, isTraining: Boolean) {
        if (visible && isTraining) {
            startSpawnTimer()
        } else {
            stopSpawnTimer()
        }
    }

    private fun startSpawnTimer() {
        if (spawnJob?.isActive == true) return
        
        spawnJob = viewModelScope.launch {
            while (true) {
                // Random delay between 5 and 20 seconds
                delay(Random.nextLong(5000, 20000).milliseconds)
                
                if (!_uiState.value.isSpriteVisible) {
                    _uiState.update { 
                        it.copy(
                            isSpriteVisible = true,
                            spritePosition = Offset(
                                0.35f + Random.nextFloat() * 0.3f,
                                0.35f + Random.nextFloat() * 0.3f
                            )
                        )
                    }
                    
                    // Sprite disappears after 5 seconds if not clicked
                    delay(5000.milliseconds)
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

    /**
     * Handles the collection of a loot box when the sprite is clicked.
     *
     * @param activeSkill The type of the skill currently being trained.
     */
    fun onSpriteClick(activeSkill: SkillType) {
        if (!_uiState.value.isSpriteVisible) return
        
        viewModelScope.launch {
            collectLootBoxUseCase(activeSkill)
            _uiState.update { it.copy(isSpriteVisible = false) }
        }
    }

    override fun onCleared() {
        stopSpawnTimer()
    }
}
