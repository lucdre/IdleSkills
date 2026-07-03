package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
class SkillTrainingManager @AssistedInject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val inventoryRepository: InventoryRepositoryInterface,
    @Assisted private val coroutineScope: CoroutineScope,
    @Assisted private val onTickStarted: (Long, Long) -> Unit,
    @Assisted private val onSkillUpdate: (Skill) -> Unit
) {
    @AssistedFactory
    interface Factory {
        fun create(
            coroutineScope: CoroutineScope,
            onTickStarted: (Long, Long) -> Unit,
            onSkillUpdate: (Skill) -> Unit
        ): SkillTrainingManager
    }

    private data class TrainingConfig(
        val method: TrainingMethod,
        val cards: List<Card>
    )

    private val _config = MutableStateFlow<TrainingConfig?>(null)
    private var activeSkillName: String? = null
    private var trainingJob: Job? = null

    companion object {
        private const val MIN_TICK_DURATION_MS = 100L
        private const val STATS_FLUSH_INTERVAL_MS = 5000L
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
            var pendingStatsCount = 0
            var lastFlushTime = System.currentTimeMillis()
            
            try {
                while (isActive) {
                    val config = _config.value ?: break
                    val effectiveDuration = config.method.getEffectiveActionDuration(config.cards)
                    
                    val batch = ActionBatcher.calculateBatch(effectiveDuration, MIN_TICK_DURATION_MS)

                    val startTime = System.currentTimeMillis()
                    onTickStarted(startTime, batch.durationMs)
                    
                    // Wait for the action to complete
                    delay(batch.durationMs.milliseconds)

                    if (!isActive) break

                    // Apply rewards
                    val currentConfig = _config.value ?: break
                    val maxAttempts = 3

                    for (attempt in 1..maxAttempts) {
                        try {
                            skillRepository.addXp(localSkillName, currentConfig.method.xpPerAction * batch.actionsCount)

                            currentConfig.method.producedItemType?.let { itemType ->
                                inventoryRepository.addItem(itemType, batch.actionsCount)
                            }

                            // Buffer stats
                            pendingStatsCount += batch.actionsCount
                            val now = System.currentTimeMillis()
                            if (now - lastFlushTime >= STATS_FLUSH_INTERVAL_MS) {
                                recordTrainingActionUseCase(currentConfig.method.skill, currentConfig.method.type, pendingStatsCount)
                                pendingStatsCount = 0
                                lastFlushTime = now
                            }

                            // Notify listeners
                            val updatedSkill = skillRepository.getSkillByName(localSkillName)
                            if (updatedSkill != null) {
                                onSkillUpdate(updatedSkill)
                            }
                            break
                        } catch (e: Exception) {
                            if (attempt == maxAttempts) {
                                Log.e("SkillTrainingManager", "Error completing action after $maxAttempts attempts", e)
                                cancelTraining()
                                return@launch
                            }
                            Log.w("SkillTrainingManager", "Training failed (attempt $attempt/$maxAttempts). Retrying...", e)
                            delay((100L * attempt).milliseconds)
                        }
                    }
                }
            } finally {
                // Flush stats even if job is canceled
                if (pendingStatsCount > 0) {
                    val finalConfig = _config.value
                    if (finalConfig != null) {
                        withContext(NonCancellable) {
                            try {
                                recordTrainingActionUseCase(finalConfig.method.skill, finalConfig.method.type, pendingStatsCount)
                            } catch (e: Exception) {
                                Log.e("SkillTrainingManager", "Failed final stats flush", e)
                            }
                        }
                    }
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
