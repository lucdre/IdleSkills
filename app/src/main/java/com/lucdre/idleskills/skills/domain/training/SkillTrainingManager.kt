package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Manages the active training process for a specific skill.
 *
 * Updates XP via [UpdateSkillUseCase] and notifies listeners about progress and skill state changes.
 *
 * @property updateSkillUseCase The use case responsible for applying XP and handling level ups.
 * @property coroutineScope The scope used to launch and manage the training coroutine.
 * @property onProgressUpdate A callback lambda function invoked periodically during a training
 *                            action to report progress (0.0f to 1.0f).
 * @property onSkillUpdate A callback lambda function invoked after a training action completes
 *                         and XP has been applied, providing the updated [Skill] object.
 */
class SkillTrainingManager(
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val coroutineScope: CoroutineScope,
    private val onProgressUpdate: (Float) -> Unit,
    private val onSkillUpdate: (Skill) -> Unit
) {
    // Name of the active skill that's currently training
    private var activeSkillName: String? = null

    // Training coroutine job
    private var trainingJob: Job? = null

    // Constants for time
    private companion object {
        const val PROGRESS_UPDATE_INTERVAL_MS = 100L
        const val BASIC_TRAINING_DURATION_MS = 1000L
    }

    /**
     * Starts training a skill using a specific [TrainingMethod] and an optional [Tool].
     *
     * @param skill The [Skill] to start training.
     * @param method The [TrainingMethod] defining the action duration and base XP.
     * @param tool The [Tool] used to modify XP gains. (Placeholder: Remove null, in the future very skill will have a basic tool)
     */
    fun startTraining(skill: Skill, method: TrainingMethod, tool: Tool? = null) {
        cancelTraining() // Cancel previous job if any
        activeSkillName = skill.name
        Log.d("SkillTrainingManager", "Starting training for ${skill.name} with ${method.name}")

        trainingJob = coroutineScope.launch {
            var currentSkill = skill // Keep track of the most recent skill state

            while (true) {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + method.actionDurationMs

                // Loop for progress updates during the action
                while (System.currentTimeMillis() < endTime) {
                    val currentTime = System.currentTimeMillis()
                    val progress = (currentTime - startTime).toFloat() / method.actionDurationMs.toFloat()
                    onProgressUpdate(progress.coerceIn(0f, 1f)) // Ensure progress stays 0-1
                    delay(PROGRESS_UPDATE_INTERVAL_MS)
                }
                onProgressUpdate(1f) // Ensure final progress is 1.0

                // Calculate XP gained for this action
                val xpGained = if (tool != null) {
                    (method.xpPerAction * tool.getXpMultiplier()).toInt()
                } else {
                    method.xpPerAction
                }

                // Apply XP update using the use case
                try {
                    val updatedSkill = updateSkillUseCase(currentSkill, xpGained)
                    currentSkill = updatedSkill // Update local state for next loop iteration
                    onSkillUpdate(updatedSkill) // Notify listener
                } catch (e: Exception) {
                    Log.e("SkillTrainingManager", "Error updating skill during training", e)
                    // TODO maybe add onError callback
                    cancelTraining()
                    break
                }
            }
        }
    }

    /**
     * (Placeholder while there are not enough training methods))
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

                // Apply XP update
                try {
                    val updatedSkill = updateSkillUseCase(currentSkill, 1)
                    currentSkill = updatedSkill
                    onSkillUpdate(updatedSkill)
                } catch (e: Exception) {
                    Log.e("SkillTrainingManager", "Error updating skill during basic training", e)
                    cancelTraining() //
                    break
                }
            }
        }
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
