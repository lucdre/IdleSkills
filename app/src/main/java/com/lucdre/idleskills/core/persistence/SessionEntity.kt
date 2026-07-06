package com.lucdre.idleskills.core.persistence

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.lucdre.idleskills.region.domain.Region

/**
 * Database entity representing the active player session and state.
 *
 * @property id Unique local identifier (singleton key, defaults to 0).
 * @property currentRegion The player's current active region.
 * @property activeSkillName Name of the skill currently being trained, or null.
 * @property activeMethodName ID of the training method currently active, or null.
 * @property lastSavedTimestamp Epoch millisecond timestamp when session state was last saved.
 */
@Entity(tableName = "player_session")
data class SessionEntity(
    @PrimaryKey val id: Int = 0,
    val currentRegion: Region = Region.FIRST_REGION,
    val activeSkillName: String? = null,
    val activeMethodName: String? = null,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)
