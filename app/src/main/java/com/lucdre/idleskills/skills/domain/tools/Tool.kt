package com.lucdre.idleskills.skills.domain.tools

/**
 * Represents a tool that can be used to train a specific skill.
 *
 * @property skillName The name of the skill this tool is used for.
 * @property name The name of the tool.
 * @property efficiency (Placeholder) The efficiency of the tool, defaults to 1.0f.
 * @property requiredLevel The required level to use the tool, defaults to 1.
 * @property iconResId The resource ID of the tool's icon, defaults to null.
 */
data class Tool(
    val skillName: String,
    val name: String,
    val efficiency: Float = 1.0f,
    val requiredLevel: Int = 1,
    val iconResId: Int? = null
)