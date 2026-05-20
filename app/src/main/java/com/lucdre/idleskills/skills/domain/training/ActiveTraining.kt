package com.lucdre.idleskills.skills.domain.training

/**
 * Represents the current active training session.
 * 
 * @property skillName The name of the skill being trained.
 * @property methodName The name of the specific training method.
 */
data class ActiveTraining(
    val skillName: String,
    val methodName: String
)
