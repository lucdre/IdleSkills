package com.lucdre.idleskills.skills.domain.tools

data class Tool(
    val skillName: String,
    val name: String,
    val efficiency: Float = 1.0f,
    val requiredLevel: Int = 1,
    val iconResId: Int? = null
) {
    /**
     * Calculate bonus XP multiplier based on tool efficiency
     */
    fun getXpMultiplier(): Float {
        return efficiency
    }
}