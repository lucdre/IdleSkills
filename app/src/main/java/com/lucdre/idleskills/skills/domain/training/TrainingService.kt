package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UI state representing the current training status.
 *
 * @property activeSkillName The skill currently being trained, or null if idle.
 * @property activeMethod The training method currently being used, or null if idle.
 * @property startTime The start time of the current training action in milliseconds.
 * @property durationMs The total duration of the current training action in milliseconds.
 * @property isPaused Whether training is currently paused.
 */
data class TrainingState(
    val activeSkillName: String? = null,
    val activeMethod: TrainingMethod? = null,
    val startTime: Long = 0L,
    val durationMs: Long = 0L,
    val isPaused: Boolean = true,
    val sessionXpGained: Int = 0
)

/**
 * Domain service that manages the training ticker and state.
 */
@Singleton
class TrainingService @Inject constructor(
    private val managerFactory: SkillTrainingManager.Factory,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val skillRepository: SkillRepositoryInterface,
    private val sessionRepository: SessionRepositoryInterface
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _trainingState = MutableStateFlow(TrainingState())
    val trainingState: StateFlow<TrainingState> = _trainingState.asStateFlow()

    private var trainingManager: SkillTrainingManager? = null
    private var cardsJob: Job? = null
    private var startXp = mutableMapOf<String, Int>()
    
    private var isAppVisible = true

    init {
        trainingManager = managerFactory.create(
            coroutineScope = serviceScope,
            onTickStarted = { startTime, durationMs ->
                _trainingState.update { it.copy(startTime = startTime, durationMs = durationMs) }
            },
            onSkillUpdate = { updatedSkill ->
                // Safety: Only update if this skill is still the active one
                if (updatedSkill.name != _trainingState.value.activeSkillName) return@create

                // Calculate session XP
                if (!startXp.containsKey(updatedSkill.name)) {
                    startXp[updatedSkill.name] = updatedSkill.xp
                }
                val currentSessionXp = updatedSkill.xp - (startXp[updatedSkill.name] ?: updatedSkill.xp)
                
                _trainingState.update { 
                    it.copy(
                        sessionXpGained = currentSessionXp
                    ) 
                }
            }
        )
    }

    fun setAppVisibility(visible: Boolean) {
        isAppVisible = visible
        if (!visible) {
            trainingManager?.cancelTraining()
        } else {
            // Re-start training if we have an active skill/method
            val state = _trainingState.value
            val skillName = state.activeSkillName
            val method = state.activeMethod
            if (skillName != null && method != null) {
                serviceScope.launch {
                    val skill = skillRepository.getSkills().find { it.name == skillName } ?: return@launch
                    // Restart logic (re-uses existing jobs if any, but startTraining handles it)
                    startTraining(skill, method)
                }
            }
        }
    }

    fun startTraining(skill: Skill, method: TrainingMethod) {
        stopTraining()

        if (!startXp.containsKey(skill.name)) {
            startXp[skill.name] = skill.xp
        }
        val currentSessionXp = skill.xp - (startXp[skill.name] ?: skill.xp)

        _trainingState.update { 
            it.copy(
                activeSkillName = skill.name,
                activeMethod = method,
                isPaused = false,
                startTime = 0L,
                durationMs = 0L,
                sessionXpGained = currentSessionXp
            )
        }

        serviceScope.launch {
            sessionRepository.setActiveTraining(skill.type, method.type)
        }

        cardsJob = serviceScope.launch {
            getActiveCardsUseCase(skill.type, method.type).collect { cards ->
                if (trainingManager?.isTraining(skill.name) == false) {
                    trainingManager?.startTraining(skill, method, cards)
                } else {
                    trainingManager?.updateCards(cards)
                }
            }
        }
    }

    fun stopTraining() {
        cardsJob?.cancel()
        trainingManager?.cancelTraining()
        
        _trainingState.update { 
            it.copy(
                activeSkillName = null,
                activeMethod = null,
                isPaused = true,
                startTime = 0L,
                durationMs = 0L
            )
        }

        serviceScope.launch {
            sessionRepository.setActiveTraining(null, null)
        }
    }
}
