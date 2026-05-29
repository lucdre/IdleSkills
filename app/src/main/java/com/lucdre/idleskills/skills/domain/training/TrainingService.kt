package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UI state representing the current training status.
 *
 * @property activeSkill The skill currently being trained, or null if idle.
 * @property activeMethod The training method currently being used, or null if idle.
 * @property progress The progress of the current training action (0 to 1.0).
 * @property isPaused Whether training is currently paused.
 */
data class TrainingState(
    val activeSkill: Skill? = null,
    val activeMethod: TrainingMethod? = null,
    val progress: Float = 0f,
    val isPaused: Boolean = true
)

/**
 * Domain service that manages the training ticker and state.
 */
@Singleton
class TrainingService @Inject constructor(
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val skillRepository: SkillRepositoryInterface
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _trainingState = MutableStateFlow(TrainingState())
    val trainingState: StateFlow<TrainingState> = _trainingState.asStateFlow()

    private var trainingManager: SkillTrainingManager? = null
    private var cardsJob: Job? = null

    init {
        trainingManager = SkillTrainingManager(
            skillRepository = skillRepository,
            recordTrainingActionUseCase = recordTrainingActionUseCase,
            coroutineScope = serviceScope,
            onProgressUpdate = { progress ->
                _trainingState.update { it.copy(progress = progress) }
            },
            onSkillUpdate = { updatedSkill ->
                _trainingState.update { it.copy(activeSkill = updatedSkill) }
            }
        )
    }

    fun toggleTraining(skill: Skill, method: TrainingMethod) {
        val currentState = _trainingState.value
        if (currentState.activeSkill?.name == skill.name && currentState.activeMethod?.name == method.name) {
            stopTraining()
        } else {
            startTraining(skill, method)
        }
    }

    fun startTraining(skill: Skill, method: TrainingMethod) {
        stopTraining()

        _trainingState.update { 
            it.copy(
                activeSkill = skill,
                activeMethod = method,
                isPaused = false,
                progress = 0f
            )
        }

        serviceScope.launch {
            skillRepository.setActiveTraining(ActiveTraining(skill.name, method.name))
        }

        cardsJob = serviceScope.launch {
            val skillType = SkillType.fromString(skill.name) ?: SkillType.WOODCUTTING
            getActiveCardsUseCase(skillType, method.name).collect { cards ->
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
                activeSkill = null,
                activeMethod = null,
                isPaused = true,
                progress = 0f
            )
        }

        serviceScope.launch {
            skillRepository.setActiveTraining(null)
        }
    }
}
