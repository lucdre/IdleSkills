package com.lucdre.idleskills.loot.domain.usecase

import androidx.compose.ui.geometry.Offset
import com.lucdre.idleskills.loot.domain.LootEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Use case that manages the lifecycle and timing of random loot spawning events.
 *
 * @property random The [Random] instance used for timing and positioning.
 */
class ObserveLootEventsUseCase @Inject constructor(
    private val random: Random
) {
    companion object {
        // Spawn timing configuration
        private const val MIN_SPAWN_DELAY_MS = 5000L
        private const val MAX_SPAWN_DELAY_MS = 240000L
        
        // Visibility timing configuration
        private const val DESPAWN_TIMEOUT_MS = 60000L
        
        // Spawn area bounds (normalized 0.0 - 1.0)
        private const val SPAWN_AREA_MIN = 0.35f
        private const val SPAWN_AREA_SIZE = 0.3f
    }

    /**
     * Starts observing loot events.
     *
     * @param interruptSignal Emits when an event (like a click)
     *                        should hide the current sprite.
     * @return A [Flow] of [LootEvent] objects.
     */
    operator fun invoke(interruptSignal: Flow<Unit>): Flow<LootEvent> = flow {
        while (true) {
            // Random delay between spawns
            delay(random.nextLong(MIN_SPAWN_DELAY_MS, MAX_SPAWN_DELAY_MS).milliseconds)
            
            val position = Offset(
                SPAWN_AREA_MIN + random.nextFloat() * SPAWN_AREA_SIZE,
                SPAWN_AREA_MIN + random.nextFloat() * SPAWN_AREA_SIZE
            )
            emit(LootEvent.Spawn(position))

            // Wait until click OR timeout
            withTimeoutOrNull(DESPAWN_TIMEOUT_MS.milliseconds) {
                interruptSignal.first()
            }
            
            emit(LootEvent.Hide)
        }
    }
}
