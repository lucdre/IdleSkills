package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.core.persistence.SessionDao
import com.lucdre.idleskills.core.persistence.SessionEntity
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing session state.
 */
@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val trainingMethodRepository: TrainingMethodRepositoryInterface
) : SessionRepositoryInterface {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sessionMutex = Mutex()
    private val _sessionCache = MutableStateFlow<SessionEntity?>(null)
    private var lastPersistedSession: SessionEntity? = null

    init {
        repositoryScope.launch {
            val dbSession = sessionDao.getSession() ?: SessionEntity()
            val sanitized = sanitizeSession(dbSession)
            _sessionCache.value = sanitized
            lastPersistedSession = sanitized
            startPeriodicSync()
        }
    }

    override suspend fun getSessionData(): SessionData {
        return getSessionInternal().let {
            SessionData(
                activeSkill = it.activeSkillName?.let { name -> SkillType.fromString(name) },
                activeMethod = it.activeMethodName?.let { id -> TrainingMethodType.fromId(id) },
                currentRegion = it.currentRegion,
                lastSavedTimestamp = it.lastSavedTimestamp
            )
        }
    }

    override fun observeCurrentRegion(): Flow<Region> {
        return _sessionCache.filterNotNull().map { it.currentRegion }
    }

    override suspend fun getCurrentRegion(): Region {
        return getSessionInternal().currentRegion
    }

    override suspend fun setCurrentRegion(region: Region) {
        updateCache { it.copy(currentRegion = region) }
        syncToPersistence()
    }

    override fun observeActiveTraining(): Flow<Pair<SkillType, TrainingMethodType>?> {
        return _sessionCache.filterNotNull().map { session ->
            val skillName = session.activeSkillName
            val methodName = session.activeMethodName
            if (skillName != null && methodName != null) {
                val skillType = SkillType.fromString(skillName)
                val methodType = TrainingMethodType.fromId(methodName)
                if (skillType != null && methodType != null) {
                    skillType to methodType
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    override suspend fun setActiveTraining(skill: SkillType?, method: TrainingMethodType?) {
        updateCache {
            it.copy(
                activeSkillName = skill?.name,
                activeMethodName = method?.id,
                lastSavedTimestamp = System.currentTimeMillis()
            )
        }
        syncToPersistence()
    }

    override suspend fun updateLastSavedTimestamp() {
        updateCache { it.copy(lastSavedTimestamp = System.currentTimeMillis()) }
    }

    override suspend fun syncToPersistence() {
        sessionMutex.withLock {
            val current = _sessionCache.value ?: return
            if (current != lastPersistedSession) {
                android.util.Log.d("IO_DEBUG", "Writing Session to Disk: $current")
                sessionDao.insertOrUpdate(current)
                lastPersistedSession = current
            }
        }
    }

    private suspend fun getSessionInternal(): SessionEntity {
        return _sessionCache.filterNotNull().first()
    }

    private suspend fun updateCache(transform: (SessionEntity) -> SessionEntity) {
        sessionMutex.withLock {
            val current = _sessionCache.value ?: sessionDao.getSession() ?: SessionEntity()
            _sessionCache.value = transform(current)
        }
    }

    /**
     * Ensures that the session data references valid skills and training methods.
     */
    private fun sanitizeSession(session: SessionEntity): SessionEntity {
        val skillName = session.activeSkillName ?: return session
        val skillType = SkillType.fromString(skillName) ?: return session.copy(
            activeSkillName = null,
            activeMethodName = null
        )

        val methodName = session.activeMethodName ?: return session
        val methodType = TrainingMethodType.fromId(methodName) ?: return session.copy(
            activeMethodName = null
        )

        val validMethods = trainingMethodRepository.getTrainingMethodsForSkill(skillType, session.currentRegion)
        val methodExists = validMethods.any { it.type == methodType }

        return if (!methodExists) {
            session.copy(activeMethodName = null)
        } else {
            session
        }
    }

    private fun startPeriodicSync() {
        repositoryScope.launch {
            while (true) {
                delay(5.minutes)
                syncToPersistence()
            }
        }
    }
}
