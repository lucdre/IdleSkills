package com.lucdre.idleskills.skills.domain.training

/**
 * Active training session.
 * 
 * @property skillName The name of the skill being trained.
 * @property methodName The name of the specific training method.
 */
data class ActiveTraining(
    val skillName: String,
    val methodName: String
)
