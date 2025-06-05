package com.lucdre.idleskills.skills.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.prestige.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.SkillTrainingManager
import com.lucdre.idleskills.skills.domain.tools.usecase.GetToolUseCase
import com.lucdre.idleskills.skills.domain.training.usecase.GetTrainingMethodUseCase
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.tools.Tool
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
 * @property getVisibleSkillsUseCase Use case for retrieving available skills at current prestige
 * @property updateSkillUseCase Use case for updating skill data
 * @property getTrainingMethodUseCase Use case for retrieving training methods
 * @property getToolUseCase Use case for retrieving tools
 */
@HiltViewModel
class SkillListViewModel @Inject constructor(
    private val getVisibleSkillsUseCase: GetVisibleSkillsUseCase,
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val getTrainingMethodUseCase: GetTrainingMethodUseCase,
    private val getToolUseCase: GetToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillListUiState(isLoading = true))
    val uiState: StateFlow<SkillListUiState> = _uiState.asStateFlow()

    // Track previous levels to detect level ups
    private val previousLevels = mutableMapOf<String, Int>()
    
    // Track selected tools per skill to maintain selection across skill switches
    private val selectedTools = mutableMapOf<String, Tool>()
    
    // Track selected training methods per skill to maintain selection across skill switches
    private val selectedMethods = mutableMapOf<String, TrainingMethod>()

    private val trainingManager = SkillTrainingManager(
        updateSkillUseCase = updateSkillUseCase,
        coroutineScope = viewModelScope,
        onProgressUpdate = { progress ->
            _uiState.value = _uiState.value.copy(trainingProgress = progress)
        },
        onSkillUpdate = { updatedSkill ->
            viewModelScope.launch {
                // Get the level before update to check for level up
                val previousLevel = previousLevels[updatedSkill.name] ?: updatedSkill.level
                Log.d("SkillListViewModel", "Level check for ${updatedSkill.name}: ${updatedSkill.level} > $previousLevel = ${updatedSkill.level > previousLevel}")

                // Update the stored level for next time
                previousLevels[updatedSkill.name] = updatedSkill.level

                // If level up, check for newly available training methods and tools
                if (updatedSkill.level > previousLevel) {
                    Log.d("SkillListViewModel", "🎉 ${updatedSkill.name} leveled up to ${updatedSkill.level}!")

                    // Fetch training methods and tools that might be available with new level
                    val (updatedMethods, updatedTools) = getUpdatedMethodsAndTools(updatedSkill)

                    // Check if there's a better tool available now
                    val hasBetterTool = checkForBetterTool(updatedSkill.name, updatedSkill.level, _uiState.value.activeTool)


                    _uiState.value = _uiState.value.copy(
                        trainingMethods = updatedMethods,
                        tools = updatedTools,
                        hasBetterToolAvailable = hasBetterTool
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
            getVisibleSkillsUseCase.observeVisibleSkills().collect { skills ->
                Log.d("SkillListViewModel", "Skills updated: ${skills.map { "${it.name}:${it.xp}" }}")
                _uiState.value = _uiState.value.copy(
                    skills = skills,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Loads visible skills from the repository based on current prestige.
     * Updates UI state.
     */
    fun loadSkills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val skills = getVisibleSkillsUseCase()
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
     * and starts training with the previously selected method and tool.
     *
     * @param skill The skill that was selected
     */
    fun onSkillClick(skill: Skill) {
        // Clicking the same skill that's already active, do nothing
        if (trainingManager.isTraining(skill.name)) return

        // Cancel any previous training
        trainingManager.cancelTraining()

        Log.d("SkillListViewModel", "Initialized previousLevel for ${skill.name}: ${skill.level}")
        previousLevels[skill.name] = skill.level

        // Fetch training methods for this skill
        val methods = getTrainingMethodUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        // Use previously selected method for this skill, or default to basic method if first time
        val selectedMethod = selectedMethods[skill.name] ?: methods.minByOrNull { it.requiredLevel }

        // Fetch tools for this skill
        val tools = getToolUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        // Use previously selected tool for this skill, or default to basic tool if first time
        val selectedTool = selectedTools[skill.name] ?: tools.minByOrNull { it.requiredLevel }
        
        // Find best available tool to check if there's a better one than the selected tool
        val hasBetterTool = checkForBetterTool(skill.name, skill.level, selectedTool)

        _uiState.value = _uiState.value.copy(
            activeSkill = skill.name,
            trainingMethods = methods,
            activeTrainingMethod = selectedMethod,
            tools = tools,
            activeTool = selectedTool,
            hasBetterToolAvailable = hasBetterTool
        )

        // Start training with selected method and selected tool
        if (selectedMethod != null) {
            trainingManager.startTraining(skill, selectedMethod, selectedTool)
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

        // Save the selected method for this skill
        _uiState.value.activeSkill?.let { skillName ->
            selectedMethods[skillName] = method
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
     * Selects and equips the best available tool for the specified skill.
     *
     * Updates the UI state with the new tool and restarts training if the skill
     * is currently being trained.
     *
     * @param skillName The name of the skill to select a better tool for
     */
    fun selectBetterTool(skillName: String) {
        val currentSkill = _uiState.value.skills.find { it.name == skillName }
        currentSkill?.let { skill ->
            val bestTool = getToolUseCase.getBestAvailableTool(skill.name, skill.level)
            if (bestTool != null && bestTool != _uiState.value.activeTool) {
                // Save the selected tool for this skill
                selectedTools[skillName] = bestTool
                
                _uiState.value = _uiState.value.copy(
                    activeTool = bestTool,
                    hasBetterToolAvailable = false // Reset the flag since we just equipped the better tool
                )

                // Restart training with the new tool
                val activeMethod = _uiState.value.activeTrainingMethod
                if (trainingManager.isTraining(skill.name) && activeMethod != null) {
                    trainingManager.startTraining(skill, activeMethod, bestTool)
                }
            }
        }
    }

    /**
     * Helper function to get updated methods and tools after a level up.
     */
    private fun getUpdatedMethodsAndTools(skill: Skill): Pair<List<TrainingMethod>, List<Tool>> {
        val updatedMethods = getTrainingMethodUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        val updatedTools = getToolUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        return Pair(updatedMethods, updatedTools)
    }

    /**
     * Helper function to check if there is a better tool available.
     */
    private fun checkForBetterTool(skillName: String, skillLevel: Int, currentTool: Tool?): Boolean {
        val bestTool = getToolUseCase.getBestAvailableTool(skillName, skillLevel)
        return bestTool != null && bestTool != currentTool
    }

    /**
     * Resets the training state to initial conditions.
     * Used when prestiging to provide a fresh start experience.
     *
     * - Cancels active training
     * - Clears selected tools and methods for all skills
     * - Resets UI state to show no active skill
     * - Clears progress indicators
     */
    fun resetTrainingState() {
        // Cancel any active training
        trainingManager.cancelTraining()

        // Clear all selected tools and methods
        selectedTools.clear()
        selectedMethods.clear()

        // Clear previous level tracking
        previousLevels.clear()

        // Reset UI state to fresh start
        _uiState.value = _uiState.value.copy(
            activeSkill = null,
            trainingMethods = emptyList(),
            activeTrainingMethod = null,
            tools = emptyList(),
            activeTool = null,
            hasBetterToolAvailable = false,
            trainingProgress = 0f
        )
    }

    /**
     * Called when the ViewModel is being destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        trainingManager.cancelTraining()
    }
}