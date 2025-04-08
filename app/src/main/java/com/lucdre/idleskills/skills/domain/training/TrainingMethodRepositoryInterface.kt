package com.lucdre.idleskills.skills.domain.training

interface TrainingMethodRepositoryInterface {
    fun getTrainingMethodsForSkill(skillName: String): List<TrainingMethod>
}