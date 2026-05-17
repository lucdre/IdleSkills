package com.lucdre.idleskills.skills.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.prestige.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.training.SkillTrainingManager
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.usecase.GetTrainingMethodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the [com.lucdre.idleskills.ui.screens.SkillListScreen].
 *
 * Manages UI state and business logic for skill list, training methods and cards.
 * Handles user interactions and communicates with domain layer use cases.
 *
 * @property getVisibleSkillsUseCase Use case for retrieving available skills at current prestige
 * @property updateSkillUseCase Use case for updating skill data
 * @property getTrainingMethodUseCase Use case for retrieving training methods
 * @property getActiveCardsUseCase Use case for retrieving active cards
 */
@HiltViewModel
class SkillListViewModel @Inject constructor(
    private val getVisibleSkillsUseCase: GetVisibleSkillsUseCase,
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val getTrainingMethodUseCase: GetTrainingMethodUseCase,
    private val getActiveCardsUseCase: GetActiveCardsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillListUiState(isLoading = true))
    val uiState: StateFlow<SkillListUiState> = _uiState.asStateFlow()

    // Track previous levels to detect level ups
    private val previousLevels = mutableMapOf<String, Int>()

    // Track selected training methods per skill to maintain selection across skill switches
    private val selectedMethods = mutableMapOf<String, TrainingMethod>()

    private val trainingManager = SkillTrainingManager(
        updateSkillUseCase = updateSkillUseCase,
        coroutineScope = viewModelScope,
        onProgressUpdate = { progress ->
            _uiState.update { it.copy(trainingProgress = progress) }
        },
        onSkillUpdate = { updatedSkill ->
            viewModelScope.launch {
                // Get the level before update to check for level up
                val previousLevel = previousLevels[updatedSkill.name] ?: updatedSkill.level
                
                // Update the stored level for next time
                previousLevels[updatedSkill.name] = updatedSkill.level

                // If level up, check for newly available training methods
                if (updatedSkill.level > previousLevel) {
                    Log.d("SkillListViewModel", "🎉 ${updatedSkill.name} leveled up to ${updatedSkill.level}!")

                    val updatedMethods = getTrainingMethodUseCase(updatedSkill.name)
                        .filter { it.requiredLevel <= updatedSkill.level }

                    _uiState.update { state ->
                        state.copy(trainingMethods = updatedMethods)
                    }
                }
            }
        }
    )

    /**
     * Initializes the ViewModel and sets up observers.
     * Loads skills and starts observing skill updates.
     */
    init {
        loadSkills()

        viewModelScope.launch {
            getVisibleSkillsUseCase.observeVisibleSkills().collect { skills ->
                _uiState.update { state ->
                    state.copy(
                        skills = skills,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Loads visible skills from the repository based on current prestige.
     * Updates UI state.
     */
    fun loadSkills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val skills = getVisibleSkillsUseCase()
                _uiState.update { it.copy(skills = skills, isLoading = false) }
            } catch (e: Exception) {
                Log.e("SkillListViewModel", "Error loading skills", e)
                _uiState.update { it.copy(error = "Failed to load skills.", isLoading = false) }
            }
        }
    }

    /**
     * Handles the selection of a skill by the user.
     *
     * Updates UI state, fetches relevant training methods and cards,
     * and starts training with the previously selected method and cards.
     *
     * @param skill The skill that was selected
     */
    fun onSkillClick(skill: Skill) {
        // Clicking the same skill that's already active, do nothing
        if (trainingManager.isTraining(skill.name)) return

        // Cancel any previous training
        trainingManager.cancelTraining()

        previousLevels[skill.name] = skill.level

        // Fetch training methods for this skill
        val methods = getTrainingMethodUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        // Use previously selected method for this skill, or default to basic method if first time
        val selectedMethod = selectedMethods[skill.name] ?: methods.minByOrNull { it.requiredLevel }

        viewModelScope.launch {
            // Fetch active cards for this skill and method
            val activeCards = getActiveCardsUseCase(skill.name, selectedMethod?.name).first()

            _uiState.update { state ->
                state.copy(
                    activeSkill = skill.name,
                    trainingMethods = methods,
                    activeTrainingMethod = selectedMethod,
                    activeCards = activeCards,
                    trainingProgress = 0f
                )
            }

            // Start training with selected method and active cards
            if (selectedMethod != null) {
                trainingManager.startTraining(skill, selectedMethod, activeCards)
            } else {
                trainingManager.startBasicTraining(skill)
            }
        }
    }

    /**
     * Handles the selection of a training method by the user.
     *
     * Updates UI state and starts training with the selected method.
     * Automatically applies relevant cards for the new method.
     *
     * @param method The training method that was selected
     */
    fun selectTrainingMethod(method: TrainingMethod) {
        // If the training method is already active, don't restart it
        if (method == _uiState.value.activeTrainingMethod) {
            return
        }

        // Save the selected method for this skill
        _uiState.value.activeSkill?.let { skillName ->
            selectedMethods[skillName] = method
        }

        val currentSkill = _uiState.value.skills.find { it.name == _uiState.value.activeSkill }

        viewModelScope.launch {
            // Fetch active cards for the new method
            val activeCards = getActiveCardsUseCase(method.skillName, method.name).first()

            _uiState.update { state ->
                state.copy(
                    activeTrainingMethod = method,
                    activeCards = activeCards,
                    trainingProgress = 0f
                )
            }

            // Start training with the new method and active cards
            currentSkill?.let {
                trainingManager.startTraining(it, method, activeCards)
            }
        }
    }

    /**
     * Resets the training state to initial conditions.
     * Used when prestiging to provide a fresh start experience.
     */
    fun resetTrainingState() {
        // Cancel any active training
        trainingManager.cancelTraining()

        // Clear all selected methods
        selectedMethods.clear()

        // Clear previous level tracking
        previousLevels.clear()

        // Reset UI state to fresh start
        _uiState.update { state ->
            state.copy(
                activeSkill = null,
                trainingMethods = emptyList(),
                activeTrainingMethod = null,
                activeCards = emptyList(),
                trainingProgress = 0f
            )
        }
    }

    /**
     * Called when the ViewModel is being destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        trainingManager.cancelTraining()
    }
}
