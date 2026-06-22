package com.lucdre.idleskills.core.domain

import android.util.Log
import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.persistence.OfflineProgressDao
import com.lucdre.idleskills.core.persistence.SessionDao
import com.lucdre.idleskills.core.persistence.InventoryDao
import com.lucdre.idleskills.core.persistence.InventoryEntity
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of the offline progress calculation.
 *
 * @property skillName The name of the skill that was trained.
 * @property earnedXp The total XP earned during the offline period.
 * @property elapsedMs The time elapsed in milliseconds, capped at 48 hours.
 */
@Immutable
data class OfflineProgressResult(
    val skillName: String,
    val earnedXp: Int,
    val elapsedMs: Long,
    val earnedItems: Map<ItemType, Int> = emptyMap()
)

/**
 * Manages the calculation and application of offline progress.
 */
@Singleton
class OfflineProgressManager @Inject constructor(
    private val sessionDao: SessionDao,
    private val offlineProgressDao: OfflineProgressDao,
    private val inventoryDao: InventoryDao,
    private val trainingMethodDispatcher: TrainingMethodRepositoryDispatcher,
    private val getActiveCardsUseCase: GetActiveCardsUseCase,
    private val skillRepository: SkillRepositoryInterface
) {

    private val mutex = Mutex()

    /**
     * Calculates and applies XP earned while the player was away.
     * Capped at 48 hours of progress.
     *
     * @return The result of the calculation, or null if no progress was made.
     */
    suspend fun calculateAndApplyOfflineProgress(): OfflineProgressResult? = mutex.withLock {
        val session = sessionDao.getSession() ?: return null
        val activeSkillName = session.activeSkillName ?: return null
        val activeMethodName = session.activeMethodName ?: return null

        val lastSaved = session.lastSavedTimestamp
        val now = System.currentTimeMillis()
        var diffMs = now - lastSaved

        // If time is negative or too small, skip
        if (diffMs < 1000) return null

        // Cap at 48 hours
        val fortyEightHoursMs = 48L * 60 * 60 * 1000
        if (diffMs > fortyEightHoursMs) {
            diffMs = fortyEightHoursMs
            Log.d("OfflineProgressManager", "Offline progress capped at 48 hours")
        }

        // Get current skill state to check XP cap
        val currentSkill = skillRepository.getSkillByName(activeSkillName) ?: return null
        val skillType = SkillType.fromString(activeSkillName) ?: return null

        // Get the training method to determine XP rate
        val methods = trainingMethodDispatcher.getTrainingMethodsForSkill(skillType, session.currentRegion)
        val method = methods.find { it.name == activeMethodName } ?: return null

        // Get active cards to calculate effective action duration
        val cards = getActiveCardsUseCase(skillType, method.name).first()
        val effectiveDuration = method.getEffectiveActionDuration(cards)

        // Calculate XP: (Diff / EffectiveActionDuration) * XpPerAction
        val actionsCompleted = (diffMs.toDouble() / effectiveDuration).toLong()
        val earnedXp = (actionsCompleted * method.xpPerAction).toInt()
        
        // Calculate actual gain accounting for 200M cap
        val maxGain = (LevelCalculator.MAX_XP - currentSkill.xp).coerceAtLeast(0)
        val actualXpGain = earnedXp.coerceAtMost(maxGain)
        
        val earnedItems = mutableMapOf<ItemType, Int>()
        method.producedItemType?.let { itemType ->
            if (actionsCompleted > 0) {
                earnedItems[itemType] = actionsCompleted.toInt()
            }
        }

        if (actualXpGain > 0 || earnedItems.isNotEmpty()) {
            // Apply XP
            offlineProgressDao.applyOfflineProgress(
                skillType.name,
                actualXpGain,
                now,
                LevelCalculator.MAX_XP
            )
            
            // Apply Items
            if (earnedItems.isNotEmpty()) {
                val inventoryEntities = earnedItems.map { (type, qty) ->
                    InventoryEntity(type.id, qty)
                }
                inventoryDao.addItems(inventoryEntities)
            }
            
            Log.d("OfflineProgressManager", "Applied $actualXpGain offline XP and items to ${skillType.name}.")
            return OfflineProgressResult(skillType.name, actualXpGain, diffMs, earnedItems)
        } else {
            // Even if no progress earned, update timestamp to prevent redundant checks
            offlineProgressDao.updateSession(session.copy(lastSavedTimestamp = now))
        }

        return null
    }
}
