package com.lucdre.idleskills.main.presentation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.domain.OfflineProgressResult
import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.core.domain.usecase.CalculateOfflineProgressUseCase
import com.lucdre.idleskills.profile.domain.usecase.GetPlayerProfileUseCase
import com.lucdre.idleskills.region.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingService
import com.lucdre.idleskills.skills.domain.training.usecase.GetAvailableTrainingMethodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    private val getPlayerProfileUseCase: GetPlayerProfileUseCase,
    private val calculateOfflineProgressUseCase: CalculateOfflineProgressUseCase,
    private val trainingService: TrainingService,
    private val sessionRepository: SessionRepositoryInterface
) : ViewModel(), DefaultLifecycleObserver {

    private val _expandedSkillName = MutableStateFlow<String?>(null)
    private val _isScreenVisible = MutableStateFlow(value = false)
    private val _offlineProgress = MutableStateFlow<OfflineProgressResult?>(null)

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
    val activeTrainingState: StateFlow<ActiveTrainingState> = combine(
        trainingService.trainingState,
        skillsState
    ) { training, skills ->
        val activeSkill = training.activeSkillName?.let { name ->
            skills.skills.find { it.name == name }
        }
        val xpPerHour = training.activeMethod?.calculateXpPerHour(skills.activeCards) ?: 0
        val xpToNextLevel = if (activeSkill != null) {
            LevelCalculator.xpToNextLevelFromTotal(activeSkill.xp, activeSkill.level)
        } else 0

        val timeToLevelUpMs = if (xpPerHour > 0) {
            ((xpToNextLevel.toDouble() / xpPerHour) * 3600 * 1000).toLong()
        } else 0L

        ActiveTrainingState(
            trainingProgress = training.progress,
            sessionXpGained = training.sessionXpGained,
            xpPerHour = xpPerHour,
            timeToLevelUpMs = timeToLevelUpMs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActiveTrainingState())

    init {
        resumeInitialTraining()
    }

    override fun onStart(owner: LifecycleOwner) {
        setAppVisibility(true)
        checkOfflineProgress()
    }

    override fun onStop(owner: LifecycleOwner) {
        setAppVisibility(false)
    }

    private fun buildSkillsPipeline(): Flow<TrainingSkillsState> {
        val skillsFlow = getVisibleSkillsUseCase.observeVisibleSkills()

        val activeSelectionFlow = trainingService.trainingState.map { 
            it.activeSkillName to it.activeMethod
        }.distinctUntilChanged()

        val availableMethodsFlow = _expandedSkillName.flatMapLatest { expandedName ->
            if (expandedName == null) flowOf(emptyList())
            else {
                skillsFlow.map { skills ->
                    skills.find { it.name == expandedName }
                }.filterNotNull().map { skill ->
                    getAvailableTrainingMethodsUseCase(skill)
                }
            }
        }

        val activeCardsFlow = activeSelectionFlow.map { it.first to it.second?.name }
            .distinctUntilChanged()
            .flatMapLatest { (skillName, methodName) ->
                if (skillName != null && methodName != null) {
                    val skillType = SkillType.fromString(skillName) ?: SkillType.WOODCUTTING
                    getActiveCardsUseCase(skillType, methodName)
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
        return combine(
            getPlayerProfileUseCase.observeProfile(),
            _offlineProgress,
            sessionRepository.observeCurrentRegion()
        ) { profile, offline, region ->
            TrainingSessionState(
                playerProfile = profile,
                offlineProgress = offline,
                regionName = region.displayName,
                isLoading = false
            )
        }
    }

    private fun resumeInitialTraining() {
        viewModelScope.launch {
            val activeTraining = sessionRepository.observeActiveTraining().first()
            if (activeTraining != null) {
                val skills = getVisibleSkillsUseCase()
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
    }

    private fun checkOfflineProgress() {
        viewModelScope.launch {
            calculateOfflineProgressUseCase()?.let { result ->
                _offlineProgress.value = result
            }
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

    fun selectTrainingMethod(methodName: String) {
        val ui = skillsState.value
        if (ui.activeTrainingMethod?.name == methodName) {
            trainingService.stopTraining()
            return
        }
        val skill = ui.skills.find { it.name == ui.expandedSkillName } ?: return
        val method = ui.trainingMethods.find { it.name == methodName } ?: return
        trainingService.startTraining(skill, method)
    }

    fun setScreenVisible(visible: Boolean) { _isScreenVisible.value = visible }
    fun dismissOfflineProgress() { _offlineProgress.value = null }
}
