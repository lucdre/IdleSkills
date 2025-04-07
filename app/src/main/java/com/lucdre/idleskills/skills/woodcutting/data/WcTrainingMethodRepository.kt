package com.lucdre.idleskills.skills.woodcutting.data

import com.lucdre.idleskills.skills.domain.TrainingMethod
import com.lucdre.idleskills.skills.domain.TrainingMethodRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WC training methods
 *
 * Skill name, Training method name, XP per action and action duration (in milliseconds)
 */
@Singleton
class WcTrainingMethodRepository @Inject constructor(): TrainingMethodRepositoryInterface{

    private val trainingMethods = mapOf(
        "Woodcutting" to listOf(
            TrainingMethod("Woodcutting", "Tree", 10, 10000), // 10 XP, 10 seconds, Level 1
            TrainingMethod("Woodcutting", "Oak Tree", 15, 10000, 5), //15 XP, 10 seconds, Level 5
            TrainingMethod("Woodcutting", "Willow Tree", 30, 15000, 20), // 30 XP, 15 seconds, Level 20
        )
    )

    override fun getTrainingMethodsForSkill(skillName: String): List<TrainingMethod> {
        return trainingMethods[skillName] ?: emptyList()
    }
}