package com.lucdre.idleskills.skills.domain.usecase

import com.lucdre.idleskills.skills.domain.Skill
import com.lucdre.idleskills.skills.domain.SkillRepository
import kotlinx.coroutines.flow.Flow

class GetSkillsUseCase (private val skillRepository: SkillRepository) {
    suspend operator fun invoke(): List<Skill> {
        return skillRepository.getSkills()
    }

    fun observeSkills(): Flow<List<Skill>> {
        return skillRepository.observeSkills()
    }
}