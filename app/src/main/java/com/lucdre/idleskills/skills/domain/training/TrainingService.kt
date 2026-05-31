package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
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
    val activeSkillName: String? = null,
    val activeMethod: TrainingMethod? = null,
    val progress: Float = 0f,
    val isPaused: Boolean = true,
    val sessionXpGained: Int = 0
)

/**
 * Domain service that manages the training ticker and state.
 */
@Singleton
class TrainingService @Inject constructor(
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val skillRepository: SkillRepositoryInterface,
    private val inventoryRepository: com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _trainingState = MutableStateFlow(TrainingState())
    val trainingState: StateFlow<TrainingState> = _trainingState.asStateFlow()

    private var trainingManager: SkillTrainingManager? = null
    private var cardsJob: Job? = null
    private var startXp = mutableMapOf<String, Int>()
    
    private var isAppVisible = true

    init {
        trainingManager = SkillTrainingManager(
            skillRepository = skillRepository,
            recordTrainingActionUseCase = recordTrainingActionUseCase,
            inventoryRepository = inventoryRepository,
            coroutineScope = serviceScope,
            onProgressUpdate = { progress ->
                _trainingState.update { it.copy(progress = progress) }
            },
            onSkillUpdate = { updatedSkill ->
                // Calculate session XP
                if (!startXp.containsKey(updatedSkill.name)) {
                    startXp[updatedSkill.name] = updatedSkill.xp
                }
                val currentSessionXp = updatedSkill.xp - (startXp[updatedSkill.name] ?: updatedSkill.xp)
                
                _trainingState.update { 
                    it.copy(
                        activeSkillName = updatedSkill.name,
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

    fun toggleTraining(skill: Skill, method: TrainingMethod) {
        val currentState = _trainingState.value
        if (currentState.activeSkillName == skill.name && currentState.activeMethod?.name == method.name) {
            stopTraining()
        } else {
            startTraining(skill, method)
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
                progress = 0f,
                sessionXpGained = currentSessionXp
            )
        }

        serviceScope.launch {
            skillRepository.setActiveTraining(ActiveTraining(skill.name, method.name))
        }

        cardsJob = serviceScope.launch {
            getActiveCardsUseCase(skill.type, method.name).collect { cards ->
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
                progress = 0f
            )
        }

        serviceScope.launch {
            skillRepository.setActiveTraining(null)
        }
    }
}
