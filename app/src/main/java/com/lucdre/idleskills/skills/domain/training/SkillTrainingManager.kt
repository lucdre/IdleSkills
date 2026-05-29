package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Manages the active training process for a specific skill.
 *
 * Updates XP, records actions via [RecordTrainingActionUseCase],
 * and notifies listeners about progress and skill state changes.
 *
 * @property recordTrainingActionUseCase The use case responsible for recording training actions.
 * @property coroutineScope The scope used to launch and manage the training coroutine.
 * @property onProgressUpdate A callback lambda function invoked periodically during a training
 *                            action to report progress (0.0f to 1.0f).
 * @property onSkillUpdate A callback lambda function invoked after a training action completes
 *                         and XP has been applied, providing the updated [Skill] object.
 */
class SkillTrainingManager(
    private val skillRepository: SkillRepositoryInterface,
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val coroutineScope: CoroutineScope,
    private val onProgressUpdate: (Float) -> Unit,
    private val onSkillUpdate: (Skill) -> Unit
) {
    private data class TrainingConfig(
        val method: TrainingMethod,
        val cards: List<Card>
    )

    private val _config = MutableStateFlow<TrainingConfig?>(null)
    private var activeSkillName: String? = null
    private var trainingJob: Job? = null

    private companion object {
        const val PROGRESS_UPDATE_INTERVAL_MS = 33L // ~30 FPS
    }

    /**
     * Starts training a skill using a specific [TrainingMethod] and a list of [Card]s.
     *
     * @param skill The [Skill] to start training.
     * @param method The [TrainingMethod] defining the action duration and base XP.
     * @param cards The list of [Card]s used to modify action duration.
     */
    fun startTraining(skill: Skill, method: TrainingMethod, cards: List<Card> = emptyList()) {
        cancelTraining()
        activeSkillName = skill.name
        _config.value = TrainingConfig(method, cards)
        
        Log.d("SkillTrainingManager", "Starting training for ${skill.name} with ${method.name}")

        trainingJob = coroutineScope.launch {
            while (isActive) {
                val startTime = System.currentTimeMillis()
                
                // 1. Action Phase
                while (isActive) {
                    val config = _config.value ?: break
                    val actionDuration = config.method.getEffectiveActionDuration(config.cards)
                    val currentTime = System.currentTimeMillis()
                    val elapsed = currentTime - startTime
                    
                    if (elapsed >= actionDuration) break

                    onProgressUpdate((elapsed.toFloat() / actionDuration).coerceIn(0f, 1f))
                    delay(PROGRESS_UPDATE_INTERVAL_MS)
                }

                if (!isActive) break
                onProgressUpdate(1f)

                // 2. Completion Phase
                val currentConfig = _config.value ?: break
                try {
                    // Atomic XP addition - no local 'currentSkill' state used
                    skillRepository.addXp(activeSkillName!!, currentConfig.method.xpPerAction)
                    recordTrainingActionUseCase(currentConfig.method.skill.displayName, currentConfig.method.name)
                    
                    // Fetch latest state just to notify UI (purely informational)
                    val updatedSkill = skillRepository.getSkills().find { it.name == activeSkillName }
                    if (updatedSkill != null) {
                        onSkillUpdate(updatedSkill)
                    }
                } catch (e: Exception) {
                    Log.e("SkillTrainingManager", "Error completing action", e)
                    cancelTraining()
                    break
                }
            }
        }
    }

    /**
     * Updates the cards providing bonuses for the current training.
     *
     * @param newCards The new list of active cards.
     */
    fun updateCards(newCards: List<Card>) {
        _config.update { it?.copy(cards = newCards) }
    }

    fun cancelTraining() {
        trainingJob?.cancel()
        trainingJob = null
        activeSkillName = null
        _config.value = null
        onProgressUpdate(0f)
    }

    fun isTraining(skillName: String): Boolean {
        return activeSkillName == skillName && trainingJob?.isActive == true
    }
}
