package com.lucdre.idleskills.main.presentation

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.domain.usecase.CalculateOfflineProgressUseCase
import com.lucdre.idleskills.core.domain.usecase.ResetAllDataUseCase
import com.lucdre.idleskills.loot.domain.usecase.CollectLootBoxUseCase
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootBoxCountUseCase
import com.lucdre.idleskills.loot.domain.usecase.OpenLootBoxUseCase
import com.lucdre.idleskills.profile.domain.usecase.GetPlayerProfileUseCase
import com.lucdre.idleskills.profile.domain.usecase.ObserveStatisticsUseCase
import com.lucdre.idleskills.region.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingService
import com.lucdre.idleskills.skills.domain.training.usecase.GetAvailableTrainingMethodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * Unified ViewModel for all training-related screens.
 *
 * Consolidates logic for skills, training sessions, loot, and player stats.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val observeLootBoxCountUseCase: ObserveLootBoxCountUseCase,
    private val collectLootBoxUseCase: CollectLootBoxUseCase,
    private val openLootBoxUseCase: OpenLootBoxUseCase,
    private val getVisibleSkillsUseCase: GetVisibleSkillsUseCase,
    private val getAvailableTrainingMethodsUseCase: GetAvailableTrainingMethodsUseCase,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val getPlayerProfileUseCase: GetPlayerProfileUseCase,
    private val observeStatisticsUseCase: ObserveStatisticsUseCase,
    private val calculateOfflineProgressUseCase: CalculateOfflineProgressUseCase,
    private val inventoryRepository: com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface,
    private val trainingService: TrainingService,
    private val skillRepository: SkillRepositoryInterface,
    private val resetAllDataUseCase: ResetAllDataUseCase
) : ViewModel(), DefaultLifecycleObserver {

    private val _expandedSkillName = MutableStateFlow<String?>(null)
    private val _isScreenVisible = MutableStateFlow(value = false)
    private val _spriteVisible = MutableStateFlow(false)
    private val _spritePosition = MutableStateFlow(Offset(0.5f, 0.5f))
    private val _lastRewards = MutableStateFlow<Map<com.lucdre.idleskills.cards.domain.CardType, Int>?>(null)
    private val _offlineProgress = MutableStateFlow<com.lucdre.idleskills.core.domain.OfflineProgressResult?>(null)

    // Side Effects
    sealed class Effect {
        object TriggerRebirth : Effect()
    }
    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var spawnJob: Job? = null

    /**
     * State for skills and training methods.
     */
    val skillsState: StateFlow<TrainingSkillsState> = buildSkillsPipeline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingSkillsState(isLoading = true))

    /**
     * State for loot boxes and rewards.
     */
    val lootState: StateFlow<TrainingLootState> = buildLootPipeline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingLootState())

    /**
     * State for player profile, stats, and inventory.
     */
    val sessionState: StateFlow<TrainingSessionState> = buildSessionPipeline()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingSessionState())

    /**
     * High-frequency training state (progress ticker).
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
        observeVisibilityAndManageTimer()
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

    private fun buildLootPipeline(): Flow<TrainingLootState> {
        return combine(
            observeLootBoxCountUseCase(),
            _lastRewards,
            _spriteVisible,
            _spritePosition
        ) { loot, rewards, spriteVisible, pos ->
            TrainingLootState(
                lootBoxes = loot,
                lastRewards = rewards,
                isSpriteVisible = spriteVisible,
                spritePosition = pos
            )
        }
    }

    private fun buildSessionPipeline(): Flow<TrainingSessionState> {
        return combine(
            getPlayerProfileUseCase.observeProfile(),
            observeStatisticsUseCase.observeStatistics(),
            _offlineProgress,
            inventoryRepository.observeItems()
        ) { profile, stats, offline, inventory ->
            TrainingSessionState(
                playerProfile = profile,
                playerStatistics = stats,
                offlineProgress = offline,
                regionName = profile.currentRegion.displayName,
                inventoryItems = inventory
            )
        }
    }

    private fun observeVisibilityAndManageTimer() {
        _isScreenVisible.combine(skillsState.map { it.activeTrainingSkill }.distinctUntilChanged()) { visible, skill ->
            visible && skill != null
        }.distinctUntilChanged()
        .onEach { shouldRun ->
            if (shouldRun) startSpawnTimer() else stopSpawnTimer()
        }.launchIn(viewModelScope)
    }

    private fun startSpawnTimer() {
        stopSpawnTimer()
        spawnJob = viewModelScope.launch {
            while (true) {
                delay(Random.nextLong(5000, 20000))
                if (!_spriteVisible.value) {
                    _spritePosition.value = Offset(0.35f + Random.nextFloat() * 0.3f, 0.35f + Random.nextFloat() * 0.3f)
                    _spriteVisible.value = true
                    delay(5000)
                    _spriteVisible.value = false
                }
            }
        }
    }

    private fun stopSpawnTimer() {
        spawnJob?.cancel()
        spawnJob = null
        _spriteVisible.value = false
    }

    private fun resumeInitialTraining() {
        viewModelScope.launch {
            val activeTraining = skillRepository.observeActiveTraining().first()
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

    fun toggleTraining(skill: Skill, method: TrainingMethod) = trainingService.toggleTraining(skill, method)

    fun setScreenVisible(visible: Boolean) { _isScreenVisible.value = visible }
    fun dismissOfflineProgress() { _offlineProgress.value = null }
    fun clearRewards() { _lastRewards.value = null }

    fun onSpriteClick() {
        val skill = skillsState.value.activeTrainingSkill ?: return
        if (!_spriteVisible.value) return
        viewModelScope.launch {
            collectLootBoxUseCase(skill)
            _spriteVisible.value = false
        }
    }

    fun onOpenBoxClick(skill: SkillType) {
        viewModelScope.launch {
            openLootBoxUseCase(skill).onSuccess { rewards -> _lastRewards.value = rewards }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            trainingService.stopTraining()
            resetAllDataUseCase()
            _effect.send(Effect.TriggerRebirth)
        }
    }
    
    fun setOfflineProgress(result: com.lucdre.idleskills.core.domain.OfflineProgressResult) {
        _offlineProgress.value = result
    }
}
