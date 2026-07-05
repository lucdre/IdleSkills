package com.lucdre.idleskills.core.util

/**
 * Game constants.
 */
object Constants {
    /**
     * Maximum XP a skill can have.
     */
    const val MAX_XP = 200_000_000

    /**
     * Maximum level a skill can reach.
     */
    const val MAX_LEVEL = 126

    /**
     * Maximum time offline progress can accumulate (48 hours).
     */
    const val OFFLINE_PROGRESS_CAP_MS = 48L * 60 * 60 * 1000
}
