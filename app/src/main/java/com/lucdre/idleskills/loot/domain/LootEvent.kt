package com.lucdre.idleskills.loot.domain

import androidx.compose.ui.geometry.Offset

/**
 * Represents events related to random loot spawning in the training scene.
 */
sealed class LootEvent {
    /**
     * Triggered when a new loot sprite should be displayed.
     *
     * @property position The screen position where the sprite should appear.
     */
    data class Spawn(val position: Offset) : LootEvent()

    /**
     * Triggered when the current loot sprite should be hidden.
     */
    object Hide : LootEvent()
}
