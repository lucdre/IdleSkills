package com.lucdre.idleskills.skills.data

import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Skill XP and Leveling repository.
 */
@Singleton
class SkillRepository @Inject constructor(
    private val skillDao: SkillDao
) : SkillRepositoryInterface {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
        skillDao.addXpAtomically(skillName, amount, LevelCalculator.MAX_XP)
    }

    override suspend fun resetSkills(skills: List<Skill>): List<Skill> {
        val entities = skills.map { SkillEntity(it.name, it.xp) }
        skillDao.insertAll(entities)
        return skills
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
