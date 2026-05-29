package com.lucdre.idleskills.skills.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.domain.usecase.ResetAllDataUseCase
import com.lucdre.idleskills.profile.domain.usecase.GetPlayerProfileUseCase
import com.lucdre.idleskills.profile.domain.usecase.ObserveStatisticsUseCase
import com.lucdre.idleskills.region.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingService
import com.lucdre.idleskills.skills.domain.training.usecase.GetAvailableTrainingMethodsUseCase
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SkillListScreen.
 *
 * Manages UI state and business logic for skill list, training methods and cards.
 * Subscribes to TrainingService for real-time training progress and state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SkillListViewModel @Inject constructor(
    private val getVisibleSkillsUseCase: GetVisibleSkillsUseCase,
    private val getAvailableTrainingMethodsUseCase: GetAvailableTrainingMethodsUseCase,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val getPlayerProfileUseCase: GetPlayerProfileUseCase,
    private val observeStatisticsUseCase: ObserveStatisticsUseCase,
    private val trainingService: TrainingService,
    private val skillRepository: SkillRepositoryInterface,
    private val resetAllDataUseCase: ResetAllDataUseCase
) : ViewModel() {

    // 1. Core State Inputs
    private val _expandedSkillName = MutableStateFlow<String?>(null)
    private val _offlineProgress = MutableStateFlow<com.lucdre.idleskills.core.domain.OfflineProgressResult?>(null)

    // 2. Reactive Pipelines
    
    // Observed Skills list (Source of Truth)
    private val skillsFlow = getVisibleSkillsUseCase.observeVisibleSkills()

    init {
        // Handle initial training resumption logic separately from state mapping
        viewModelScope.launch {
            val skills = skillsFlow.first { it.isNotEmpty() }
            resumeInitialTraining(skills)
        }
    }

    // Methods are re-loaded when expansion changes OR when skill data (levels) update
    private val availableMethodsFlow = combine(
        _expandedSkillName,
        skillsFlow
    ) { expandedName, skills ->
        expandedName to skills
    }.flatMapLatest { (name, skills) ->
        if (name == null) flowOf(emptyList())
        else {
            val skill = skills.find { it.name == name }
            if (skill != null) {
                flow { emit(getAvailableTrainingMethodsUseCase(skill)) }
            } else {
                flowOf(emptyList())
            }
        }
    }

    // Combine infrequently updating data
    private val baseStateFlow = combine(
        skillsFlow,
        getPlayerProfileUseCase.observeProfile(),
        observeStatisticsUseCase.observeStatistics(),
        availableMethodsFlow,
        _expandedSkillName
    ) { skills, profile, stats, methods, expanded ->
        SkillListUiState(
            skills = skills,
            playerProfile = profile,
            playerStatistics = stats,
            trainingMethods = methods,
            expandedSkillName = expanded
        )
    }

    // High-frequency cards update
    private val activeCardsFlow = trainingService.trainingState.flatMapLatest { training ->
        val skillName = training.activeSkill?.name
        val skillType = if (skillName != null) SkillType.fromString(skillName) else null
        
        if (skillType != null && training.activeMethod != null) {
            getActiveCardsUseCase(skillType, training.activeMethod.name)
        } else {
            flowOf(emptyList())
        }
    }

    // Final UI State
    val uiState: StateFlow<SkillListUiState> = combine(
        baseStateFlow,
        trainingService.trainingState,
        activeCardsFlow,
        _offlineProgress
    ) { base, training, cards, offline ->
        base.copy(
            isLoading = false,
            activeSkill = training.activeSkill?.name,
            activeTrainingMethod = training.activeMethod,
            trainingProgress = training.progress,
            activeCards = cards,
            offlineProgress = offline
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SkillListUiState(isLoading = true))

    // --- Actions ---

    /**
     * Toggles expansion of a skill card.
     */
    fun toggleSkillExpansion(skillName: String) {
        val current = _expandedSkillName.value
        _expandedSkillName.value = if (current == skillName) null else skillName
    }

    /**
     * Toggles training for a specific method.
     */
    fun selectTrainingMethod(method: TrainingMethod) {
        val skill = uiState.value.skills.find { it.name == method.skill.displayName }
        if (skill != null) {
            trainingService.toggleTraining(skill, method)
        }
    }

    /**
     * Skill header click logic (purely expansion).
     */
    fun onSkillClick(skill: Skill) {
        toggleSkillExpansion(skill.name)
    }

    private suspend fun resumeInitialTraining(skills: List<Skill>) {
        val activeTraining = skillRepository.observeActiveTraining().firstOrNull()
        if (activeTraining != null) {
            val skill = skills.find { it.name == activeTraining.skillName }
            if (skill != null) {
                val methods = getAvailableTrainingMethodsUseCase(skill)
                val method = methods.find { it.name == activeTraining.methodName }
                if (method != null) {
                    trainingService.startTraining(skill, method)
                    _expandedSkillName.value = skill.name
                }
            }
        }
    }

    fun dismissOfflineProgress() {
        _offlineProgress.value = null
    }

    fun resetTrainingState() {
        trainingService.stopTraining()
        _expandedSkillName.value = null
    }

    fun resetAllData(context: Context) {
        viewModelScope.launch {
            trainingService.stopTraining()
            resetAllDataUseCase()
            ProcessPhoenix.triggerRebirth(context)
        }
    }

    fun setOfflineProgress(result: com.lucdre.idleskills.core.domain.OfflineProgressResult) {
        _offlineProgress.value = result
    }
}
