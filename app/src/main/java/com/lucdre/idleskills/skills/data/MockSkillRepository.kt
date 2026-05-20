package com.lucdre.idleskills.skills.data

import android.util.Log
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock repository, will be replaced by something better in the future.
 */
@Singleton
class MockSkillRepository @Inject constructor() : SkillRepositoryInterface {

    private val _skills = MutableStateFlow(
        listOf(
            Skill("Woodcutting", level = 1, xp = 0),
            Skill("Mining", level = 1, xp = 0),
            Skill("Fishing", level = 1, xp = 0),
            Skill("Smelting"),
            Skill("Cooking"),
            Skill("Smithing")
        )
    )

    private val _activeTraining = MutableStateFlow<ActiveTraining?>(null)

    override fun observeSkills(): Flow<List<Skill>> {
        return _skills.asStateFlow()
    }

    override suspend fun getSkills(): List<Skill> {
        return _skills.value
    }

    override suspend fun updateSkill(skill: Skill): Skill {
        val currentSkills = _skills.value.toMutableList()
        val index = currentSkills.indexOfFirst { it.name == skill.name }

        if (index != -1) {
            currentSkills[index] = skill
            _skills.value = currentSkills
        }

        return skill
    }

    override fun observeActiveTraining(): Flow<ActiveTraining?> {
        return _activeTraining.asStateFlow()
    }

    override suspend fun setActiveTraining(training: ActiveTraining?) {
        _activeTraining.value = training
    }

    override suspend fun resetSkills(skills: List<Skill>): List<Skill> {
        Log.d("MockSkillRepository", "Resetting all skills to level 1, XP 0")
        _skills.value = skills
        return _skills.value
    }
}
