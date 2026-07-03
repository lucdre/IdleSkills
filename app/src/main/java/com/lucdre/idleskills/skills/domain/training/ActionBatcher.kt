package com.lucdre.idleskills.skills.domain.training

/**
 * Represents a batch of actions bundled into a single engine tick.
 *
 * @property actionsCount The number of actions to apply in this tick.
 * @property durationMs The calculated duration of the tick in milliseconds.
 */
data class ActionBatch(
    val actionsCount: Int,
    val durationMs: Long
)

/**
 * Logic for bundling training actions
 */
object ActionBatcher {
    /**
     * Determines how many actions should be performed in the next tick.
     *
     * @param effectiveDurationMs The duration of a single action.
     * @param minTickDurationMs The minimum threshold for a single engine tick.
     * @return An [ActionBatch] defining the workload for the next engine cycle.
     */
    fun calculateBatch(effectiveDurationMs: Double, minTickDurationMs: Long): ActionBatch {
        val safeDuration = effectiveDurationMs.coerceAtLeast(0.001)

        return if (safeDuration < minTickDurationMs) {
            val count = (minTickDurationMs / safeDuration).toInt().coerceAtLeast(1)
            ActionBatch(
                actionsCount = count,
                durationMs = (safeDuration * count).toLong()
            )
        } else {
            ActionBatch(
                actionsCount = 1,
                durationMs = safeDuration.toLong()
            )
        }
    }
}
