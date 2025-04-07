package com.lucdre.idleskills.skills.domain

import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun observeSkills(): Flow<List<Skill>>
    suspend fun getSkills(): List<Skill>
    suspend fun updateSkill(skill: Skill): Skill
}