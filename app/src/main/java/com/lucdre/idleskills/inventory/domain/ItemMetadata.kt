package com.lucdre.idleskills.inventory.domain

import androidx.compose.runtime.Immutable

/**
 * Visual and descriptive metadata for an item.
 *
 * @property displayName Name of the item.
 * @property iconResId Resource ID for the item's icon.
 * @property description Description.
 */
@Immutable
data class ItemMetadata(
    val displayName: String,
    val iconResId: Int,
    val description: String = ""
)
