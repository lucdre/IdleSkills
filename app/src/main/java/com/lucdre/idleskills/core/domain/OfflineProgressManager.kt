package com.lucdre.idleskills.core.domain

import android.util.Log
import com.lucdre.idleskills.core.persistence.ProfileDao
import com.lucdre.idleskills.core.persistence.SkillDao
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryDispatcher
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
 *
 * This manager calculates the time elapsed since the last save and awards XP
 * for the player's active training method, capped at 48 hours.
 */
@Singleton
class OfflineProgressManager @Inject constructor(
    private val profileDao: ProfileDao,
    private val skillDao: SkillDao,
    private val trainingMethodDispatcher: TrainingMethodRepositoryDispatcher
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
        val methods = trainingMethodDispatcher.getTrainingMethodsForSkill(activeSkillName, profile.currentRegion)
        val method = methods.find { it.name == activeMethodName } ?: return null

        // Calculate XP: (Diff / ActionDuration) * XpPerAction
        val actionsCompleted = diffMs / method.actionDurationMs
        val earnedXp = (actionsCompleted * method.xpPerAction).toInt()

        var result: OfflineProgressResult? = null

        if (earnedXp > 0) {
            val skills = skillDao.getSkills()
            val skillEntity = skills.find { it.name == activeSkillName }
            
            if (skillEntity != null) {
                val currentSkill = Skill(
                    name = skillEntity.name,
                    xp = skillEntity.xp,
                    level = LevelCalculator.calculateLevelFromTotalXp(skillEntity.xp)
                )
                
                // Add XP and check for level up
                val updatedXp = currentSkill.xp + earnedXp
                val updatedLevel = LevelCalculator.calculateLevelFromTotalXp(updatedXp)
                
                // Persist the update
                skillDao.insertOrUpdate(skillEntity.copy(xp = updatedXp))
                
                Log.d("OfflineProgressManager", 
                    "Applied $earnedXp offline XP to $activeSkillName. " +
                    "Level: ${currentSkill.level} -> $updatedLevel"
                )

                result = OfflineProgressResult(activeSkillName, earnedXp, diffMs)
            }
        }

        // Update the timestamp to 'now' after applying progress
        profileDao.insertOrUpdate(profile.copy(lastSavedTimestamp = now))
        
        return result
    }
}
