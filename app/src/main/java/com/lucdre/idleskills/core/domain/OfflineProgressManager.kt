package com.lucdre.idleskills.core.domain

import android.util.Log
import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.util.Constants
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryDispatcher
import kotlin.math.floor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculation result for offline gains.
 *
 * @property skillName The name of the skill that was trained.
 * @property earnedXp The total XP earned during the offline period.
 * @property elapsedMs The time elapsed in milliseconds, capped at 48 hours.
 * @property earnedItems Map of items earned during the offline period.
 */
@Immutable
data class OfflineProgressResult(
    val skillName: String,
    val earnedXp: Int,
    val elapsedMs: Long,
    val earnedItems: Map<ItemType, Int> = emptyMap()
)

/**
 * Syncs progress made while the app was closed.
 */
@Singleton
class OfflineProgressManager @Inject constructor(
    private val sessionRepository: SessionRepositoryInterface,
    private val gameActionRepository: GameActionRepositoryInterface,
    private val trainingMethodDispatcher: TrainingMethodRepositoryDispatcher,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val skillRepository: SkillRepositoryInterface
) {

    private val mutex = Mutex()

    /**
     * Calculates and applies gains. Capped at 48h.
     *
     * @return The result of the calculation, or null if no progress was made.
     */
    suspend fun calculateAndApplyOfflineProgress(): OfflineProgressResult? = mutex.withLock {
        val session = sessionRepository.getSessionData()
        val activeSkill = session.activeSkill ?: return null
        val activeMethod = session.activeMethod ?: return null

        val lastSaved = session.lastSavedTimestamp
        val now = System.currentTimeMillis()
        var diffMs = now - lastSaved

        // If time is negative or too small, skip
        if (diffMs < 1000) return null

        // Cap time
        val capMs = Constants.OFFLINE_PROGRESS_CAP_MS
        if (diffMs > capMs) {
            diffMs = capMs
            Log.d("OfflineProgressManager", "Offline progress capped at ${capMs / 3600000} hours")
        }

        // Get current skill state to check XP cap
        val currentSkill = skillRepository.getSkillByName(activeSkill.name) ?: return null

        // Get the training method to determine XP rate
        val methods = trainingMethodDispatcher.getTrainingMethodsForSkill(activeSkill, session.currentRegion)
        val method = methods.find { it.type == activeMethod } ?: return null

        // Get active cards to calculate effective action duration
        val cards = getActiveCardsUseCase(activeSkill, method.type).first()
        val effectiveDuration = method.getEffectiveActionDuration(cards)

        // Calculate XP: (Diff / EffectiveActionDuration) * XpPerAction
        val actionsCompleted = floor(diffMs.toDouble() / effectiveDuration).toLong()
        val earnedXp = (actionsCompleted * method.xpPerAction).toInt()
        
        // Calculate actual gain accounting for 200M cap
        val maxGain = (LevelCalculator.MAX_XP - currentSkill.xp).coerceAtLeast(0)
        val actualXpGain = earnedXp.coerceAtMost(maxGain)

        val earnedItems = if (actionsCompleted > 0 && method.producedItemType != null) {
            mapOf(method.producedItemType to actionsCompleted.toInt())
        } else {
            emptyMap()
        }

        if (actualXpGain > 0 || earnedItems.isNotEmpty()) {
            // Apply progress atomically
            gameActionRepository.applyOfflineProgress(
                skillName = activeSkill.name,
                xpAmount = actualXpGain,
                items = earnedItems,
                now = now
            )
            
            Log.d("OfflineProgressManager", "Applied $actualXpGain offline XP and items to ${activeSkill.name}.")
            return OfflineProgressResult(activeSkill.name, actualXpGain, diffMs, earnedItems)
        } else {
            // Even if no progress earned, update timestamp to prevent redundant checks
            sessionRepository.updateLastSavedTimestamp()
        }

        return null
    }
}
