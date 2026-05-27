package com.lucdre.idleskills.core.persistence

import androidx.room3.TypeConverter
import com.lucdre.idleskills.region.domain.Region

/**
 * Type converters for Room to handle non-primitive types.
 */
class Converters {
    @TypeConverter
    fun fromRegion(value: Region): String {
        return value.name
    }

    @TypeConverter
    fun toRegion(value: String): Region {
        // Use runCatching to handle cases where an enum value was renamed in code 
        // but still exists in the user's database.
        return runCatching { Region.valueOf(value) }
            .getOrElse { Region.FIRST_REGION }
    }

    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        return value.joinToString("|")
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        return if (value.isEmpty()) emptySet() else value.split("|").toSet()
    }
}
