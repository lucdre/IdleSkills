package com.lucdre.idleskills.core.persistence

import androidx.room.TypeConverter
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
        return Region.valueOf(value)
    }

    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        return if (value.isEmpty()) emptySet() else value.split(",").toSet()
    }
}
