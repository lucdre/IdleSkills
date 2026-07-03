package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Handles the training loop for a skill.
 *
 * @property skillRepository Interface for the skill repository.
 * @property recordTrainingActionUseCase The use case responsible for recording training actions.
 * @property inventoryRepository Interface for the inventory repository.
 * @property coroutineScope The scope used to launch and manage the training coroutine.
 * @property onTickStarted A callback lambda function invoked at the start of each training
 *                        tick, providing the start time and duration.
 * @property onSkillUpdate A callback lambda function invoked after a training action completes
 *                         and XP has been applied, providing the updated [Skill] object.
 */
class SkillTrainingManager(
    private val skillRepository: SkillRepositoryInterface,
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val inventoryRepository: InventoryRepositoryInterface,
    private val coroutineScope: CoroutineScope,
    private val onTickStarted: (Long, Long) -> Unit,
    private val onSkillUpdate: (Skill) -> Unit
) {
    private data class TrainingConfig(
        val method: TrainingMethod,
        val cards: List<Card>
    )

    private val _config = MutableStateFlow<TrainingConfig?>(null)
    private var activeSkillName: String? = null
    private var trainingJob: Job? = null

    companion object {
        private const val MIN_TICK_DURATION_MS = 100L
    }

    /**
     * Starts training.
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
                val effectiveDuration = config.method.getEffectiveActionDuration(config.cards)
                
                // Safety: If actions are faster than MIN_TICK_DURATION_MS, batch them into a MIN_TICK_DURATION_MS tick
                val actionsInTick: Int
                val tickDurationMs: Long
                if (effectiveDuration < MIN_TICK_DURATION_MS) {
                    actionsInTick = (MIN_TICK_DURATION_MS / effectiveDuration).toInt().coerceAtLeast(1)
                    tickDurationMs = (effectiveDuration * actionsInTick).toLong()
                } else {
                    actionsInTick = 1
                    tickDurationMs = effectiveDuration.toLong()
                }

                val startTime = System.currentTimeMillis()
                onTickStarted(startTime, tickDurationMs)
                
                // Wait for the action to complete
                delay(tickDurationMs.milliseconds)

                if (!isActive) break

                // Apply rewards
                val currentConfig = _config.value ?: break
                try {
                    skillRepository.addXp(localSkillName, currentConfig.method.xpPerAction * actionsInTick)

                    currentConfig.method.producedItemType?.let { itemType ->
                        inventoryRepository.addItem(itemType, actionsInTick)
                    }

                    recordTrainingActionUseCase(currentConfig.method.skill, currentConfig.method.type, actionsInTick)

                    // Notify listeners
                    // Always get latest state from repository
                    val updatedSkill = skillRepository.getSkillByName(localSkillName)
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
        onTickStarted(0L, 0L)
    }

    fun isTraining(skillName: String): Boolean {
        return activeSkillName == skillName && trainingJob?.isActive == true
    }
}
