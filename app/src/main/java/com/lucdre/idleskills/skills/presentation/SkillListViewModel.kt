package com.lucdre.idleskills.skills.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.skills.domain.usecase.GetSkillsUseCase
import com.lucdre.idleskills.skills.domain.Skill
import com.lucdre.idleskills.skills.domain.TrainingMethod
import com.lucdre.idleskills.skills.domain.usecase.GetTrainingMethodUseCase
import com.lucdre.idleskills.skills.domain.usecase.UpdateSkillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillListViewModel @Inject constructor(
    private val getSkillsUseCase: GetSkillsUseCase,
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val getTrainingMethodUseCase: GetTrainingMethodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillListUiState(isLoading = true))
    val uiState: StateFlow<SkillListUiState> = _uiState.asStateFlow()

    // Track the active skill that's currently incrementing
    private var activeSkill: String? = null

    // Track the auto-increment job so we can cancel it
    private var incrementJob: Job? = null

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

    fun onSkillClick(skill: Skill) {
        // If we click the same skill that's already active, do nothing
        if (skill.name == activeSkill) return

        // Cancel any previous increment job
        incrementJob?.cancel()

        // Start training the new skill
        activeSkill = skill.name

        // Fetch training methods for this skill
        val methods = getTrainingMethodUseCase(skill.name)
            .filter { it.requiredLevel <= skill.level }

        // Find best available method and set it as active by default, will change it to the previously used training method once persistence is implemented. //TODO
        val bestMethod = getTrainingMethodUseCase.getBestAvailableMethod(skill.name, skill.level)

        _uiState.value = _uiState.value.copy(
            activeSkill = skill.name,
            trainingMethods = methods,
            activeTrainingMethod = bestMethod
        )

        // Start training with the best method if available
        bestMethod?.let { startTraining(skill, it) } ?: startBasicTraining(skill)
    }

    private fun startTraining(skill: Skill, method: TrainingMethod) {
        // Cancel previous job if any
        incrementJob?.cancel()

        incrementJob = viewModelScope.launch {
            while (true) {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + method.actionDurationMs

                // Update progress every 100ms during the action
                while (System.currentTimeMillis() < endTime) {
                    val currentTime = System.currentTimeMillis()
                    val progress = (currentTime - startTime).toFloat() / method.actionDurationMs.toFloat()
                    _uiState.value = _uiState.value.copy(trainingProgress = progress)
                    delay(100) // Update progress 10 times per second
                }

                // Set to completed
                _uiState.value = _uiState.value.copy(trainingProgress = 1f)

                // Action completed, add XP
                val currentSkill = _uiState.value.skills.find { it.name == skill.name }
                currentSkill?.let { updateSkillWithXp(it, method.xpPerAction) }
            }
        }
    }

    /**
     *  Basic training for when there are no training methods, remove in the future.
     */
    private fun startBasicTraining(skill: Skill) {
        incrementJob = viewModelScope.launch {
            while (true) {
                _uiState.value = _uiState.value.copy(trainingProgress = 0f)

                val startTime = System.currentTimeMillis()
                val endTime = startTime + 1000 // 1 second

                while (System.currentTimeMillis() < endTime) {
                    val currentTime = System.currentTimeMillis()
                    val progress = (currentTime - startTime).toFloat() / 1000f
                    _uiState.value = _uiState.value.copy(trainingProgress = progress)
                    delay(100)
                }

                _uiState.value = _uiState.value.copy(trainingProgress = 1f)

                val currentSkill = _uiState.value.skills.find { it.name == skill.name }
                currentSkill?.let { updateSkillWithXp(it, 1) }
            }
        }
    }

    fun selectTrainingMethod(method: TrainingMethod) {

        // If the training method is already active, don't restart it
        if (method == _uiState.value.activeTrainingMethod) {
            return
        }

        _uiState.value = _uiState.value.copy(
            activeTrainingMethod = method,
            trainingProgress = 0f)

        // Find the current skill and start training with the new method
        val currentSkill = _uiState.value.skills.find { it.name == activeSkill }
        currentSkill?.let { startTraining(it, method) }
    }

    private suspend fun updateSkillWithXp(skill: Skill, xpAmount: Int) {
        try {
            // Add custom XP
            val updatedSkill = updateSkillUseCase(skill, xpAmount)

            // If level up, check for newly available training methods
            if (updatedSkill.level > skill.level) {
                Log.d("SkillListViewModel", "🎉 ${skill.name} leveled up to ${updatedSkill.level}!")

                // Fetch updated training methods that might be available with new level
                val updatedMethods = getTrainingMethodUseCase(skill.name)
                    .filter { it.requiredLevel <= updatedSkill.level }

                _uiState.value = _uiState.value.copy(trainingMethods = updatedMethods)
            }
        } catch (e: Exception) {
            Log.e("SkillListViewModel", "Error updating skill", e)
            _uiState.value = _uiState.value.copy(error = "Failed to update skill.")
        }
    }

    // Cancel the job when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        incrementJob?.cancel()
    }
}
