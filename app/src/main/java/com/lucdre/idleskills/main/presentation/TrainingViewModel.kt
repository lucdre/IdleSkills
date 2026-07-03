package com.lucdre.idleskills.main.presentation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.region.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.LevelInfo
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import com.lucdre.idleskills.skills.domain.training.TrainingService
import com.lucdre.idleskills.skills.domain.training.TrainingSessionManager
import com.lucdre.idleskills.skills.domain.training.usecase.GetAvailableTrainingMethodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for training screens.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getVisibleSkillsUseCase: GetVisibleSkillsUseCase,
    private val getAvailableTrainingMethodsUseCase: GetAvailableTrainingMethodsUseCase,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val trainingService: TrainingService,
    private val trainingSessionManager: TrainingSessionManager
) : ViewModel(), DefaultLifecycleObserver {

    private val _expandedSkillName = MutableStateFlow<String?>(null)
    private val _isScreenVisible = MutableStateFlow(value = false)

    /**
     * State for skills and training methods.
     */
    val skillsState: StateFlow<TrainingSkillsState> = buildSkillsPipeline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingSkillsState(isLoading = true))

    /**
     * State for player profile and session info.
     */
    val sessionState: StateFlow<TrainingSessionState> = buildSessionPipeline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingSessionState(isLoading = true))

    /**
     * Progress ticker state.
     */
    val activeTrainingState: StateFlow<ActiveTrainingState> = buildActiveTrainingPipeline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActiveTrainingState())

    init {
        trainingSessionManager.initializeSession()
    }

    override fun onStart(owner: LifecycleOwner) {
        setAppVisibility(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        setAppVisibility(false)
        viewModelScope.launch {
            trainingSessionManager.syncSession()
        }
    }

    private fun buildActiveTrainingPipeline(): Flow<ActiveTrainingState> {
        val xpPerHourFlow = combine(
            trainingService.trainingState.map { it.activeMethod }.distinctUntilChanged(),
            skillsState.map { it.activeCards }.distinctUntilChanged()
        ) { method, cards ->
            method?.calculateXpPerHour(cards) ?: 0
        }.distinctUntilChanged()

        return combine(
            trainingService.trainingState,
            skillsState,
            xpPerHourFlow
        ) { training, skills, xpPerHour ->
            val activeSkill = training.activeSkillName?.let { name ->
                skills.skills.find { it.name == name }
            }
            val xpToNextLevel = if (activeSkill != null) {
                LevelCalculator.xpToNextLevelFromTotal(activeSkill.xp, activeSkill.level)
            } else 0

            val timeToLevelUpMs = LevelCalculator.calculateTimeToLevelUpMs(xpPerHour, xpToNextLevel)

            ActiveTrainingState(
                startTime = training.startTime,
                durationMs = training.durationMs,
                sessionXpGained = training.sessionXpGained,
                xpPerHour = xpPerHour,
                timeToLevelUpMs = timeToLevelUpMs
            )
        }
    }

    private fun buildSkillsPipeline(): Flow<TrainingSkillsState> {
        val skillsFlow = getVisibleSkillsUseCase.observeVisibleSkills()

        val activeSelectionFlow = trainingService.trainingState.map { 
            it.activeSkillName to it.activeMethod
        }.distinctUntilChanged()

        val availableMethodsFlow = _expandedSkillName.flatMapLatest { expandedName ->
            if (expandedName == null) flowOf(emptyList())
            else {
                skillsFlow.mapNotNull { skills ->
                    skills.find { it.name == expandedName }
                }.map { skill ->
                    getAvailableTrainingMethodsUseCase(skill)
                }
            }
        }

        val activeCardsFlow = activeSelectionFlow.map { it.first to it.second?.type }
            .distinctUntilChanged()
            .flatMapLatest { (skillName, methodType) ->
                if (skillName != null && methodType != null) {
                    val skillType = SkillType.fromString(skillName) ?: SkillType.WOODCUTTING
                    getActiveCardsUseCase(skillType, methodType)
                } else {
                    flowOf(emptyList())
                }
            }

        return combine(
            skillsFlow, activeSelectionFlow, availableMethodsFlow, activeCardsFlow, _expandedSkillName
        ) { skills, selection, methods, cards, expanded ->
            val activeSkillName = selection.first
            val activeSkill = activeSkillName?.let { name -> skills.find { it.name == name } }
            val levelInfo = activeSkill?.let { LevelCalculator.getLevelInfo(it.xp) } ?: LevelInfo()

            TrainingSkillsState(
                skills = skills,
                trainingMethods = methods,
                expandedSkillName = expanded,
                activeTrainingSkill = activeSkill?.type,
                activeTrainingMethod = selection.second,
                activeCards = cards,
                levelInfo = levelInfo,
                isLoading = false
            )
        }
    }

    private fun buildSessionPipeline(): Flow<TrainingSessionState> {
        return trainingSessionManager.sessionData.map { data ->
            TrainingSessionState(
                playerProfile = data.playerProfile,
                offlineProgress = data.offlineProgress,
                regionName = data.region.displayName,
                isLoading = false
            )
        }
    }

    // --- Public Actions ---

    fun setAppVisibility(visible: Boolean) {
        trainingService.setAppVisibility(visible)
    }

    fun toggleSkillExpansion(skillName: String) {
        _expandedSkillName.update { if (it == skillName) null else skillName }
    }

    fun selectSkill(skillType: SkillType) = toggleSkillExpansion(skillType.name)

    fun selectTrainingMethod(methodType: TrainingMethodType) {
        val ui = skillsState.value
        val skill = ui.skills.find { it.name == ui.expandedSkillName } ?: return
        val method = ui.trainingMethods.find { it.type == methodType } ?: return
        trainingSessionManager.toggleTraining(skill, method)
    }

    fun setScreenVisible(visible: Boolean) { _isScreenVisible.value = visible }
    fun dismissOfflineProgress() { trainingSessionManager.dismissOfflineProgress() }
}
