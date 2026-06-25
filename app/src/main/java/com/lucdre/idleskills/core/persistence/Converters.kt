package com.lucdre.idleskills.core.persistence

import androidx.room3.ColumnTypeConverter
import com.lucdre.idleskills.region.domain.Region

/**
 * Type converters for Room to handle non-primitive types.
 */
class Converters {
    @ColumnTypeConverter
    fun fromRegion(value: Region): String {
        return value.name
    }

    @ColumnTypeConverter
    fun toRegion(value: String): Region {
        // Fallback for renamed enums
        return runCatching { Region.valueOf(value) }
            .getOrElse { Region.FIRST_REGION }
    }

    @ColumnTypeConverter
    fun fromStringSet(value: Set<String>): String {
        return value.joinToString("|")
    }

    @ColumnTypeConverter
    fun toStringSet(value: String): Set<String> {
        return if (value.isEmpty()) emptySet() else value.split("|").toSet()
    }
}
