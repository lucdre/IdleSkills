package com.lucdre.idleskills.skills.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Database entity representing progress of a single skill.
 *
 * @property name The unique name of the skill.
 * @property xp The accumulated experience points for this skill.
 */
@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val name: String,
    val xp: Int = 0
)
