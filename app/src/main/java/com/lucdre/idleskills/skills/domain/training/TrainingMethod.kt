package com.lucdre.idleskills.skills.domain.training

data class TrainingMethod(
    val skillName: String,
    val name: String,
    val xpPerAction: Int,
    val actionDurationMs: Long,
    val requiredLevel: Int = 1
) {
    /**
     * Calculate XP/h
     */
    fun calculateXpPerHour(): Int {
        val actionsPerHour = (3600 * 1000) / actionDurationMs
        return (actionsPerHour * xpPerAction).toInt()
    }
}