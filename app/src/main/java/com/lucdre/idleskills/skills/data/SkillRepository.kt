package com.lucdre.idleskills.skills.data

import com.lucdre.idleskills.core.persistence.ProfileDao
import com.lucdre.idleskills.core.persistence.ProfileEntity
import com.lucdre.idleskills.core.persistence.SkillDao
import com.lucdre.idleskills.core.persistence.SkillEntity
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing skills and active training.
 */
@Singleton
class SkillRepository @Inject constructor(
    private val skillDao: SkillDao,
    private val profileDao: ProfileDao
) : SkillRepositoryInterface {

    private val initialSkills = SkillType.entries.map { Skill(it.displayName) }

    override fun observeSkills(): Flow<List<Skill>> {
        return skillDao.observeSkills().map { entities ->
            if (entities.isEmpty()) {
                // Return defaults if DB is empty, but don't save yet to avoid side effects in observe
                initialSkills
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun getSkills(): List<Skill> {
        val entities = skillDao.getSkills()
        return if (entities.isEmpty()) {
            // Initialize DB with defaults if empty
            val defaultEntities = initialSkills.map { SkillEntity(it.name, it.xp) }
            skillDao.insertAll(defaultEntities)
            initialSkills
        } else {
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateSkill(skill: Skill): Skill {
        skillDao.insertOrUpdate(SkillEntity(skill.name, skill.xp))
        // Update the timestamp in profile whenever something important happens (like XP gain)
        updateLastSavedTimestamp()
        return skill
    }

    override fun observeActiveTraining(): Flow<ActiveTraining?> {
        return profileDao.observeProfile().map { entity ->
            if (entity?.activeSkillName != null && entity.activeMethodName != null) {
                ActiveTraining(entity.activeSkillName, entity.activeMethodName)
            } else {
                null
            }
        }
    }

    override suspend fun setActiveTraining(training: ActiveTraining?) {
        val currentProfile = profileDao.getProfile() ?: ProfileEntity()
        profileDao.insertOrUpdate(
            currentProfile.copy(
                activeSkillName = training?.skillName,
                activeMethodName = training?.methodName,
                lastSavedTimestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun resetSkills(skills: List<Skill>): List<Skill> {
        val entities = skills.map { SkillEntity(it.name, it.xp) }
        skillDao.insertAll(entities)
        updateLastSavedTimestamp()
        return skills
    }

    private suspend fun updateLastSavedTimestamp() {
        val currentProfile = profileDao.getProfile() ?: ProfileEntity()
        profileDao.insertOrUpdate(currentProfile.copy(lastSavedTimestamp = System.currentTimeMillis()))
    }

    private fun SkillEntity.toDomain(): Skill {
        return Skill(
            name = name,
            xp = xp,
            level = LevelCalculator.calculateLevelFromTotalXp(xp)
        )
    }
}
