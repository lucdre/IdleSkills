package com.lucdre.idleskills.skills.domain

interface TrainingMethodRepositoryInterface {
    fun getTrainingMethodsForSkill(skillName: String): List<TrainingMethod>
}