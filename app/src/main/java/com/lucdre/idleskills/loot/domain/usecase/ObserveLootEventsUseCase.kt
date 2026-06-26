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
    /**
     * Starts observing loot events.
     *
     * @param interruptSignal Emits when an event (like a click)
     *                        should hide the current sprite.
     * @return A [Flow] of [LootEvent] objects.
     */
    operator fun invoke(interruptSignal: Flow<Unit>): Flow<LootEvent> = flow {
        while (true) {
            // Random delay between 5 and 20 seconds
            delay(random.nextLong(5000, 20000).milliseconds)
            
            val position = Offset(
                0.35f + random.nextFloat() * 0.3f,
                0.35f + random.nextFloat() * 0.3f
            )
            emit(LootEvent.Spawn(position))

            // Wait for 5 seconds OR until click
            withTimeoutOrNull(5000.milliseconds) {
                interruptSignal.first()
            }
            
            emit(LootEvent.Hide)
        }
    }
}
