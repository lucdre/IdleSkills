package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.core.persistence.SessionDao
import com.lucdre.idleskills.core.persistence.SessionEntity
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
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
    private val sessionDao: SessionDao
) : SessionRepositoryInterface {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sessionMutex = Mutex()
    private val _sessionCache = MutableStateFlow<SessionEntity?>(null)
    private var lastPersistedSession: SessionEntity? = null

    init {
        repositoryScope.launch {
            val initialSession = sessionDao.getSession() ?: SessionEntity()
            _sessionCache.value = initialSession
            lastPersistedSession = initialSession
            startPeriodicSync()
        }
    }

    override suspend fun getSessionData(): SessionData {
        return getSessionInternal().let {
            SessionData(
                activeSkillName = it.activeSkillName,
                activeMethodName = it.activeMethodName,
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

    override fun observeActiveTraining(): Flow<ActiveTraining?> {
        return _sessionCache.filterNotNull().map { session ->
            if (session.activeSkillName != null && session.activeMethodName != null) {
                ActiveTraining(session.activeSkillName, session.activeMethodName)
            } else {
                null
            }
        }
    }

    override suspend fun setActiveTraining(training: ActiveTraining?) {
        updateCache {
            it.copy(
                activeSkillName = training?.skillName,
                activeMethodName = training?.methodName,
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

    private fun startPeriodicSync() {
        repositoryScope.launch {
            while (true) {
                delay(5.minutes)
                syncToPersistence()
            }
        }
    }
}
