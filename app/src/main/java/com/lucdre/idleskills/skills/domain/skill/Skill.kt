package com.lucdre.idleskills.skills.domain.skill

data class Skill(
    val name: String,
    val level: Int = 1, // Default level to 1
    val xp: Int = 0 // Default XP to 0
)