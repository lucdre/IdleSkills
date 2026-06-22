package com.lucdre.idleskills.skills.data

import com.lucdre.idleskills.core.persistence.SessionDao
import com.lucdre.idleskills.core.persistence.SessionEntity
import com.lucdre.idleskills.core.persistence.SkillDao
import com.lucdre.idleskills.core.persistence.SkillEntity
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing skills and active training.
 */
@Singleton
class SkillRepository @Inject constructor(
    private val skillDao: SkillDao,
    private val sessionDao: SessionDao,
) : SkillRepositoryInterface {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastTimestampUpdate = 0L
    private val timestampUpdateInterval = 60_000L // 60 seconds

    init {
        repositoryScope.launch {
            ensureSkillsExist()
        }
    }

    private suspend fun ensureSkillsExist() {
        val entities = skillDao.getSkills()
        val existingNames = entities.map { it.name }.toSet()
        val missingTypes = SkillType.entries.filter { it.name !in existingNames }
        
        if (missingTypes.isNotEmpty()) {
            skillDao.insertAll(missingTypes.map { SkillEntity(it.name, 0) })
        }
    }

    override fun observeSkills(): Flow<List<Skill>> {
        return skillDao.observeSkills().map { entities ->
            entities.map { it.toDomain() }
                .sortedBy { it.type.ordinal }
        }
    }

    override suspend fun getSkills(): List<Skill> {
        return skillDao.getSkills().map { it.toDomain() }
            .sortedBy { it.type.ordinal }
    }

    override suspend fun getSkillByName(name: String): Skill? {
        return skillDao.getSkillByName(name)?.toDomain()
    }

    override suspend fun addXp(skillName: String, amount: Int) {
        skillDao.addXpAtomically(skillName, amount, com.lucdre.idleskills.core.util.Constants.MAX_XP)
        updateLastSavedTimestamp()
    }

    override fun observeActiveTraining(): Flow<ActiveTraining?> {
        return sessionDao.observeSession().map { session ->
            if ((session?.activeSkillName != null) && (session.activeMethodName != null)) {
                ActiveTraining(session.activeSkillName, session.activeMethodName)
            } else {
                null
            }
        }
    }

    override suspend fun setActiveTraining(training: ActiveTraining?) {
        val currentSession = sessionDao.getSession() ?: SessionEntity()
        sessionDao.insertOrUpdate(
            currentSession.copy(
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
        val now = System.currentTimeMillis()
        if (now - lastTimestampUpdate >= timestampUpdateInterval) {
            val currentSession = sessionDao.getSession() ?: SessionEntity()
            sessionDao.insertOrUpdate(currentSession.copy(lastSavedTimestamp = now))
            lastTimestampUpdate = now
        }
    }

    private fun SkillEntity.toDomain(): Skill {
        val skillType = SkillType.fromString(name) ?: SkillType.WOODCUTTING // Fallback
        return Skill(
            type = skillType,
            xp = xp,
            level = LevelCalculator.calculateLevelFromTotalXp(xp)
        )
    }
}
