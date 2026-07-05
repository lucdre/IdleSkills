package com.lucdre.idleskills.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.lucdre.idleskills.inventory.domain.ItemRegistry

/**
 * CompositionLocal providing the [ItemRegistry] to UI components.
 */
val LocalItemRegistry = staticCompositionLocalOf<ItemRegistry> {
    error("No ItemRegistry provided")
}
