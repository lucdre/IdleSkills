package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import com.lucdre.idleskills.skills.domain.training.usecase.RecordTrainingActionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Manages the active training process for a specific skill.
 *
 * Updates XP via [UpdateSkillUseCase], records actions via [RecordTrainingActionUseCase],
 * and notifies listeners about progress and skill state changes.
 *
 * @property updateSkillUseCase The use case responsible for applying XP and handling level ups.
 * @property recordTrainingActionUseCase The use case responsible for recording training actions.
 * @property coroutineScope The scope used to launch and manage the training coroutine.
 * @property onProgressUpdate A callback lambda function invoked periodically during a training
 *                            action to report progress (0.0f to 1.0f).
 * @property onSkillUpdate A callback lambda function invoked after a training action completes
 *                         and XP has been applied, providing the updated [Skill] object.
 */
class SkillTrainingManager(
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val recordTrainingActionUseCase: RecordTrainingActionUseCase,
    private val coroutineScope: CoroutineScope,
    private val onProgressUpdate: (Float) -> Unit,
    private val onSkillUpdate: (Skill) -> Unit
) {
    // Name of the active skill that's currently training
    private var activeSkillName: String? = null

    // Training coroutine job
    private var trainingJob: Job? = null

    // Active training method
    private var activeMethod: TrainingMethod? = null

    // Active cards providing bonuses
    private var activeCards: List<Card> = emptyList()

    // Constants for time
    private companion object {
        const val PROGRESS_UPDATE_INTERVAL_MS = 33L // ~30 FPS
        const val BASIC_TRAINING_DURATION_MS = 1000L
    }

    /**
     * Starts training a skill using a specific [TrainingMethod] and a list of [Card]s.
     *
     * @param skill The [Skill] to start training.
     * @param method The [TrainingMethod] defining the action duration and base XP.
     * @param cards The list of [Card]s used to modify action duration.
     */
    fun startTraining(skill: Skill, method: TrainingMethod, cards: List<Card> = emptyList()) {
        cancelTraining() // Cancel previous job if any
        activeSkillName = skill.name
        activeMethod = method
        activeCards = cards
        Log.d("SkillTrainingManager", "Starting training for ${skill.name} with ${method.name}")

        trainingJob = coroutineScope.launch {
            var currentSkill = skill // Keep track of the most recent skill state

            while (true) {
                val startTime = System.currentTimeMillis()
                
                // Use the dynamically updatable method and cards
                val method = activeMethod ?: break
                val actionDuration = method.getEffectiveActionDuration(activeCards)
                val endTime = startTime + actionDuration.toLong()

                // Loop for progress updates during the action
                while (true) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime >= endTime) break

                    // Re-calculate based on current state (method or cards might have changed)
                    val currentMethod = activeMethod ?: break
                    val currentDuration = currentMethod.getEffectiveActionDuration(activeCards)
                    val progress = (currentTime - startTime).toFloat() / currentDuration
                    onProgressUpdate(progress.coerceIn(0f, 1f))
                    
                    delay(PROGRESS_UPDATE_INTERVAL_MS)
                }

                onProgressUpdate(1f) // Action complete

                // Calculate XP gained using current method state
                val xpGained = activeMethod?.xpPerAction ?: 0

                // Apply XP update and record action using use cases
                try {
                    // Record action count
                    activeMethod?.let { method ->
                        recordTrainingActionUseCase(method.skillName, method.name)
                    }

                    val updatedSkill = updateSkillUseCase(currentSkill, xpGained)
                    currentSkill = updatedSkill // Update local state for next loop iteration
                    onSkillUpdate(updatedSkill) // Notify listener
                } catch (e: Exception) {
                    Log.e("SkillTrainingManager", "Error updating skill during training", e)
                    cancelTraining()
                    break
                }
            }
        }
    }

    /**
     * (Placeholder while there are not enough training methods)
     * Starts a basic training loop for a skill, granting 1 XP per second.
     *
     * @param skill The [Skill] to start basic training for.
     */
    fun startBasicTraining(skill: Skill) {
        cancelTraining() // Cancel previous job if any
        activeSkillName = skill.name
        Log.d("SkillTrainingManager", "Starting basic training for ${skill.name}")

        trainingJob = coroutineScope.launch {
            var currentSkill = skill // Keep track of the most recent skill state

            while (true) {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + BASIC_TRAINING_DURATION_MS

                // Loop for progress updates during the action
                while (System.currentTimeMillis() < endTime) {
                    val currentTime = System.currentTimeMillis()
                    val progress = (currentTime - startTime).toFloat() / BASIC_TRAINING_DURATION_MS.toFloat()
                    onProgressUpdate(progress.coerceIn(0f, 1f))
                    delay(PROGRESS_UPDATE_INTERVAL_MS)
                }
                onProgressUpdate(1f)

                // Apply XP update and record action
                try {
                    // Record action count for basic training (using skill name and generic "Basic")
                    recordTrainingActionUseCase(currentSkill.name, "Basic")

                    // TODO changed XP for now
                    val updatedSkill = updateSkillUseCase(currentSkill, 300000000)
                    currentSkill = updatedSkill
                    onSkillUpdate(updatedSkill)
                } catch (e: Exception) {
                    Log.e("SkillTrainingManager", "Error updating skill during basic training", e)
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
        this.activeCards = newCards
    }

    /**
     * Updates the training method for the current training loop.
     *
     * @param method The new training method to apply.
     */
    fun updateMethod(method: TrainingMethod) {
        this.activeMethod = method
    }

    /**
     * Cancels any currently active training job and clears the active skill state.
     */
    fun cancelTraining() {
        if (trainingJob?.isActive == true) {
            Log.d("SkillTrainingManager", "Cancelling training for $activeSkillName")
            trainingJob?.cancel()
        }
        trainingJob = null
        activeSkillName = null
        activeMethod = null
        activeCards = emptyList()
        onProgressUpdate(0f)
    }

    /**
     * Checks if a specific skill is currently being trained.
     *
     * @param skillName The name of the skill to check.
     * @return `true` if the specified skill is actively training, `false` otherwise.
     */
    fun isTraining(skillName: String): Boolean {
        return activeSkillName == skillName && trainingJob?.isActive == true
    }
}
