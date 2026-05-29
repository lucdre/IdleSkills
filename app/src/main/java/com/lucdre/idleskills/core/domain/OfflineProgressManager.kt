package com.lucdre.idleskills.core.domain

import android.util.Log
import com.lucdre.idleskills.cards.domain.usecase.GetActiveCardsUseCase
import com.lucdre.idleskills.core.persistence.OfflineProgressDao
import com.lucdre.idleskills.core.persistence.ProfileDao
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of the offline progress calculation.
 *
 * @property skillName The name of the skill that was trained.
 * @property earnedXp The total XP earned during the offline period.
 * @property elapsedMs The time elapsed in milliseconds, capped at 48 hours.
 */
data class OfflineProgressResult(
    val skillName: String,
    val earnedXp: Int,
    val elapsedMs: Long
)

/**
 * Manages the calculation and application of offline progress.
 */
@Singleton
class OfflineProgressManager @Inject constructor(
    private val profileDao: ProfileDao,
    private val offlineProgressDao: OfflineProgressDao,
    private val trainingMethodDispatcher: TrainingMethodRepositoryDispatcher,
    private val getActiveCardsUseCase: GetActiveCardsUseCase
) {

    /**
     * Calculates and applies XP earned while the player was away.
     * Capped at 48 hours of progress.
     *
     * @return The result of the calculation, or null if no progress was made.
     */
    suspend fun calculateAndApplyOfflineProgress(): OfflineProgressResult? {
        val profile = profileDao.getProfile() ?: return null
        val activeSkillName = profile.activeSkillName ?: return null
        val activeMethodName = profile.activeMethodName ?: return null

        val lastSaved = profile.lastSavedTimestamp
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

        // Get the training method to determine XP rate
        val skillType = SkillType.fromString(activeSkillName) ?: return null
        val methods = trainingMethodDispatcher.getTrainingMethodsForSkill(skillType, profile.currentRegion)
        val method = methods.find { it.name == activeMethodName } ?: return null

        // Get active cards to calculate effective action duration
        val cards = getActiveCardsUseCase(skillType, method.name).first()
        val effectiveDuration = method.getEffectiveActionDuration(cards)

        // Calculate XP: (Diff / EffectiveActionDuration) * XpPerAction
        val actionsCompleted = diffMs / effectiveDuration
        val earnedXp = (actionsCompleted * method.xpPerAction).toInt()

        if (earnedXp > 0) {
            offlineProgressDao.applyOfflineProgress(activeSkillName, earnedXp, now)
            
            Log.d("OfflineProgressManager", "Applied $earnedXp offline XP to $activeSkillName.")
            return OfflineProgressResult(activeSkillName, earnedXp, diffMs)
        } else {
            // Even if no XP earned, update timestamp to prevent redundant checks
            offlineProgressDao.updateProfile(profile.copy(lastSavedTimestamp = now))
        }

        return null
    }
}
