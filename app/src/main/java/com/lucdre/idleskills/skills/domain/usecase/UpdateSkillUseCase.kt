package com.lucdre.idleskills.skills.domain.usecase

import android.util.Log
import com.lucdre.idleskills.skills.domain.LevelCalculator
import com.lucdre.idleskills.skills.domain.Skill
import com.lucdre.idleskills.skills.domain.SkillRepository

class UpdateSkillUseCase(private val skillRepository: SkillRepository) {
    // Default version that adds 1 XP (for backward compatibility/default scenario with no training methods)
    suspend operator fun invoke(skill: Skill): Skill {
        return invoke(skill, 1)
    }

    // Allows specifying the XP amount
    suspend operator fun invoke(skill: Skill, xpAmount: Int): Skill {
        // Increase XP by the specified amount
        var updatedSkill = skill.copy(xp = skill.xp + xpAmount)

        // Check if level up
        updatedSkill = LevelCalculator.checkForLevelUp(updatedSkill)

        // If level up, log it
        if (updatedSkill.level > skill.level) {
            Log.d("UpdateSkillUseCase", "🎉 ${skill.name} leveled up to ${updatedSkill.level}!")
        }

        return skillRepository.updateSkill(updatedSkill)
    }
}