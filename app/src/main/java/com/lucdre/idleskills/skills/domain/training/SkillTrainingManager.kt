package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Manages the active training process for a specific skill.
 *
 * Updates XP, records actions, awards items,
 * and notifies listeners about progress and skill state changes.
 *
 * @property skillRepository Interface for the skill repository.
 * @property recordTrainingActionUseCase The use case responsible for recording training actions.
 * @property inventoryRepository Interface for the inventory repository.
 * @property coroutineScope The scope used to launch and manage the training coroutine.
 * @property onProgressUpdate A callback lambda function invoked periodically during a training
 *                            action to report progress (0.0f to 1.0f).
 * @property onSkillUpdate A callback lambda function invoked after a training action completes
 *                         and XP has been applied, providing the updated [Skill] object.
 */
class SkillTrainingManager(
    private val skillRepository: SkillRepositoryInterface,
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val inventoryRepository: InventoryRepositoryInterface,
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
            val localSkillName = skill.name
            
            while (isActive) {
                val config = _config.value ?: break
                val actionDuration = config.method.getEffectiveActionDuration(config.cards)
                val startTime = System.currentTimeMillis()
                
                // 1. Action Phase
                while (isActive) {
                    val currentTime = System.currentTimeMillis()
                    val elapsed = currentTime - startTime
                    
                    if (elapsed >= actionDuration) break

                    onProgressUpdate((elapsed.toFloat() / actionDuration).coerceIn(0f, 1f))
                    
                    // Dynamic delay: update progress frequently but not too often.
                    // Aim for ~30 FPS for progress bar if duration is short, 
                    // or 10 FPS if duration is long.
                    val remaining = actionDuration - elapsed
                    val nextDelay = if (actionDuration < 1000) 16L else 100L
                    delay(nextDelay.coerceAtMost(remaining.toLong()))
                }

                if (!isActive) break
                onProgressUpdate(1f)

                // 2. Completion Phase
                val currentConfig = _config.value ?: break
                try {
                    skillRepository.addXp(localSkillName, currentConfig.method.xpPerAction)

                    currentConfig.method.producedItemType?.let { itemType ->
                        inventoryRepository.addItem(itemType, 1)
                    }

                    recordTrainingActionUseCase(currentConfig.method.skill, currentConfig.method.name)

                    // Notify listeners
                    // SSOT: Always get the latest skill state from the repository
                    val updatedSkill = skillRepository.getSkills().find { it.name == localSkillName }
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
