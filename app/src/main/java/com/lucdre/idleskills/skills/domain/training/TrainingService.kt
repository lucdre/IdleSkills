package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * State of the current training session.
 */
data class TrainingState(
    val activeSkill: Skill? = null,
    val activeMethod: TrainingMethod? = null,
    val progress: Float = 0f,
    val isPaused: Boolean = true
)

/**
 * Domain service that manages the training ticker and state.
 * Lives for the duration of the app as a Singleton.
 */
@Singleton
class TrainingService @Inject constructor(
    private val updateSkillUseCase: UpdateSkillUseCase,
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
        // Initialize manager with callbacks that update the StateFlow
        trainingManager = SkillTrainingManager(
            updateSkillUseCase = updateSkillUseCase,
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

    /**
     * Starts or pauses training for a skill and method.
     */
    fun toggleTraining(skill: Skill, method: TrainingMethod) {
        val currentState = _trainingState.value
        
        if (currentState.activeSkill?.name == skill.name && 
            currentState.activeMethod?.name == method.name && 
            !currentState.isPaused) {
            // Same skill and method is already training, so pause it
            stopTraining()
        } else {
            // Start or switch to new training
            startTraining(skill, method)
        }
    }

    fun startTraining(skill: Skill, method: TrainingMethod) {
        stopTraining() // Ensure previous is stopped

        _trainingState.update { 
            it.copy(
                activeSkill = skill,
                activeMethod = method,
                isPaused = false,
                progress = 0f
            )
        }

        // Persist active training in repository
        serviceScope.launch {
            skillRepository.setActiveTraining(ActiveTraining(skill.name, method.name))
        }

        // Observe active cards and update manager
        cardsJob = serviceScope.launch {
            getActiveCardsUseCase(skill.name, method.name).collect { cards ->
                if (!trainingManager!!.isTraining(skill.name)) {
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
