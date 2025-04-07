package com.lucdre.idleskills.skills.data

import com.lucdre.idleskills.skills.domain.Skill
import com.lucdre.idleskills.skills.domain.SkillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class MockSkillRepository @Inject constructor() : SkillRepository {

    private val _skills = MutableStateFlow(
        listOf(
            Skill("Woodcutting"),
            Skill("Firemaking"),
            Skill("Mining"),
            Skill("Smithing"),
            Skill("Fishing"),
            Skill("Cooking")
        )
    )

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
            val oldXp = currentSkills[index].xp
            Log.d("MockSkillRepository", "Changing XP from $oldXp to ${skill.xp}")

            currentSkills[index] = skill
            _skills.value = currentSkills

            Log.d("MockSkillRepository", "Updated skill list. New XP: ${_skills.value[index].xp}")
        }

        return skill
    }
}