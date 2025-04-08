package com.lucdre.idleskills.skills.domain.training

import android.util.Log
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.skill.usecase.UpdateSkillUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SkillTrainingManager(
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val coroutineScope: CoroutineScope,
    private val onProgressUpdate: (Float) -> Unit,
    private val onSkillUpdate: (Skill) -> Unit // Changed parameter to receive just the updated skill
) {
    // Track the active skill that's currently training
    private var activeSkill: String? = null

    // Track the training job so we can cancel it
    private var trainingJob: Job? = null

    fun startTraining(skill: Skill, method: TrainingMethod, tool: Tool? = null) {
        // Cancel previous job if any
        trainingJob?.cancel()

        activeSkill = skill.name

        trainingJob = coroutineScope.launch {
            var currentSkill = skill

            while (true) {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + method.actionDurationMs

                // Update progress every 100ms during the action
                while (System.currentTimeMillis() < endTime) {
                    val currentTime = System.currentTimeMillis()
                    val progress =
                        (currentTime - startTime).toFloat() / method.actionDurationMs.toFloat()
                    onProgressUpdate(progress)
                    delay(100)
                }

                // Set to completed
                onProgressUpdate(1f)

                val xpGained = if (tool != null) {
                    (method.xpPerAction * tool.getXpMultiplier()).toInt()
                } else {
                    method.xpPerAction
                }

                val updatedSkill = updateSkillUseCase(currentSkill, xpGained)
                currentSkill = updatedSkill

                // Update the skill
                onSkillUpdate(updatedSkill)
            }
        }
    }

    /**
     * Temporary function while I don't have methods and tools for everything
     */
    fun startBasicTraining(skill: Skill) {
        trainingJob?.cancel()

        activeSkill = skill.name

        var currentSkill = skill

        trainingJob = coroutineScope.launch {
            while (true) {
                onProgressUpdate(0f)

                val startTime = System.currentTimeMillis()
                val endTime = startTime + 1000 // 1 second

                while (System.currentTimeMillis() < endTime) {
                    val currentTime = System.currentTimeMillis()
                    val progress = (currentTime - startTime).toFloat() / 1000f
                    onProgressUpdate(progress)
                    delay(100)
                }

                onProgressUpdate(1f)

                val updatedSkill = updateSkillUseCase(currentSkill, 1)
                currentSkill = updatedSkill

                onSkillUpdate(updatedSkill)
            }
        }
    }

    fun cancelTraining() {
        Log.d("SkillTrainingManager", "Cancelling training")
        trainingJob?.cancel()
        activeSkill = null
    }

    fun isTraining(skillName: String): Boolean {
        return activeSkill == skillName && trainingJob?.isActive == true
    }
}
