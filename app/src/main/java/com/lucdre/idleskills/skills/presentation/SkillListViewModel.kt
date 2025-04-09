package com.lucdre.idleskills.skills.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.skills.domain.skill.usecase.GetSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.SkillTrainingManager
import com.lucdre.idleskills.skills.domain.tools.usecase.GetToolUseCase
import com.lucdre.idleskills.skills.domain.training.usecase.GetTrainingMethodUseCase
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the skill list screen.
 *
 * Manages UI state and business logic for skill list, training methods and tools.
 * Handles user interactions and communicates with domain layer use cases.
 *
 * @property getSkillsUseCase Use case for retrieving available skills
 * @property updateSkillUseCase Use case for updating skill data
 * @property getTrainingMethodUseCase Use case for retrieving training methods
 * @property getToolUseCase Use case for retrieving tools
 */
@HiltViewModel
class SkillListViewModel @Inject constructor(
    private val getSkillsUseCase: GetSkillsUseCase,
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val getTrainingMethodUseCase: GetTrainingMethodUseCase,
    private val getToolUseCase: GetToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillListUiState(isLoading = true))
    val uiState: StateFlow<SkillListUiState> = _uiState.asStateFlow()

    private val trainingManager = SkillTrainingManager(
        updateSkillUseCase = updateSkillUseCase,
        coroutineScope = viewModelScope,
        onProgressUpdate = { progress ->
            _uiState.value = _uiState.value.copy(trainingProgress = progress)
        },
        onSkillUpdate = { updatedSkill ->
            viewModelScope.launch {
                // Get the level before update to check for level up
                val previousLevel = _uiState.value.skills
                    .find { it.name == updatedSkill.name }?.level ?: updatedSkill.level

                // If level up, check for newly available training methods and tools
                if (updatedSkill.level > previousLevel) {
                    Log.d("SkillListViewModel", "🎉 ${updatedSkill.name} leveled up to ${updatedSkill.level}!")

                    // Fetch updated training methods that might be available with new level
                    val updatedMethods = getTrainingMethodUseCase(updatedSkill.name)
                        .filter { it.requiredLevel <= updatedSkill.level }

                    // Fetch updated tools that might be available with new level
                    //TODO doesn't work properly for now, only after re-clicking
                    val updatedTools = getToolUseCase(updatedSkill.name)
                        .filter { it.requiredLevel <= updatedSkill.level }

                    _uiState.value = _uiState.value.copy(
                        trainingMethods = updatedMethods,
                        tools = updatedTools
                    )
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
            getSkillsUseCase.observeSkills().collect { skills ->
                Log.d("SkillListViewModel", "Skills updated: ${skills.map { "${it.name}:${it.xp}" }}")
                _uiState.value = _uiState.value.copy(
                    skills = skills,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Loads available skills from the repository.
     * Updates UI state.
     */
    fun loadSkills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val skills = getSkillsUseCase()
                _uiState.value = _uiState.value.copy(skills = skills, isLoading = false)
            } catch (e: Exception) {
                Log.e("SkillListViewModel", "Error loading skills", e)
                _uiState.value = _uiState.value.copy(error = "Failed to load skills.", isLoading = false)
            }
        }
    }

    /**
     * Handles the selection of a skill by the user.
     *
     * Updates UI state, fetches relevant training methods and tools,
     * and starts training with the best available method and tool (Placeholder).
     *
     * @param skill The skill that was selected
     */
    fun onSkillClick(skill: Skill) {
        // Clicking the same skill that's already active, do nothing
        if (trainingManager.isTraining(skill.name)) return

        // Cancel any previous training
        trainingManager.cancelTraining()

        // Fetch training methods for this skill
        val methods = getTrainingMethodUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        // Find best available method and set it as active by default
        val bestMethod = getTrainingMethodUseCase.getBestAvailableMethod(skill.name, skill.level)

        // Fetch tools for this skill
        val tools = getToolUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        // Find best available tool
        val bestTool = getToolUseCase.getBestAvailableTool(skill.name, skill.level)

        _uiState.value = _uiState.value.copy(
            activeSkill = skill.name,
            trainingMethods = methods,
            activeTrainingMethod = bestMethod,
            tools = tools,
            activeTool = bestTool
        )

        // Start training with best method and tool if available
        if (bestMethod != null) {
            trainingManager.startTraining(skill, bestMethod, bestTool)
        } else {
            trainingManager.startBasicTraining(skill)
        }
    }

    /**
     * Handles the selection of a training method by the user.
     *
     * Updates UI state and starts training with the selected method
     * and currently active tool.
     *
     * @param method The training method that was selected
     */
    fun selectTrainingMethod(method: TrainingMethod) {
        // If the training method is already active, don't restart it
        if (method == _uiState.value.activeTrainingMethod) {
            return
        }

        _uiState.value = _uiState.value.copy(
            activeTrainingMethod = method,
            trainingProgress = 0f)

        // Find the current skill and start training with the new method
        val currentSkill = _uiState.value.skills.find { it.name == _uiState.value.activeSkill }
        val activeTool = _uiState.value.activeTool
        currentSkill?.let {
            trainingManager.startTraining(it, method, activeTool)
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