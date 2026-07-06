package com.lucdre.idleskills.loot.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Database entity representing unopened loot boxes.
 *
 * @property skillName The name of the skill this loot box is associated with.
 * @property count The number of unopened loot boxes owned.
 */
@Entity(tableName = "loot_boxes")
data class LootBoxEntity(
    @PrimaryKey val skillName: String,
    val count: Int
)
