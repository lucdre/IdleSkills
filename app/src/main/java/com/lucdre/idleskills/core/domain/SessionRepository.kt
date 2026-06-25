package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.core.persistence.SessionDao
import com.lucdre.idleskills.core.persistence.SessionEntity
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepositoryInterface {

    override suspend fun getSessionData(): SessionData? {
        return sessionDao.getSession()?.let {
            SessionData(
                activeSkillName = it.activeSkillName,
                activeMethodName = it.activeMethodName,
                currentRegion = it.currentRegion,
                lastSavedTimestamp = it.lastSavedTimestamp
            )
        }
    }

    override fun observeCurrentRegion(): Flow<Region> {
        return sessionDao.observeSession().map { it?.currentRegion ?: Region.FIRST_REGION }
    }

    override suspend fun getCurrentRegion(): Region {
        return sessionDao.getSession()?.currentRegion ?: Region.FIRST_REGION
    }

    override suspend fun setCurrentRegion(region: Region) {
        val currentSession = sessionDao.getSession() ?: SessionEntity()
        sessionDao.insertOrUpdate(currentSession.copy(currentRegion = region))
    }

    override fun observeActiveTraining(): Flow<ActiveTraining?> {
        return sessionDao.observeSession().map { session ->
            if (session?.activeSkillName != null && session.activeMethodName != null) {
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

    override suspend fun updateLastSavedTimestamp() {
        val currentSession = sessionDao.getSession() ?: SessionEntity()
        sessionDao.insertOrUpdate(
            currentSession.copy(lastSavedTimestamp = System.currentTimeMillis())
        )
    }
}
