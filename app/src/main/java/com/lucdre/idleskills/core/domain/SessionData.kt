package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.region.domain.Region

/**
 * Snapshot of the player's current session state.
 */
data class SessionData(
    val activeSkillName: String?,
    val activeMethodName: String?,
    val currentRegion: Region,
    val lastSavedTimestamp: Long
)
