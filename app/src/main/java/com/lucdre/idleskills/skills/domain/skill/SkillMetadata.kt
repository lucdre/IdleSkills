package com.lucdre.idleskills.skills.domain.skill

import androidx.compose.ui.graphics.Color
import com.lucdre.idleskills.R

/**
 * Visual configuration for a skill's UI elements.
 */
data class SkillTheme(
    val primaryColor: Color,
    val selectedBgColor: Color,
    val indicatorColor: Color,
    val unselectedIconTint: Color,
    val panelBackgroundColor: Color,
    val iconResId: Int,
    val biomeColors: List<Color>
)

/**
 * Central registry for skill metadata, including themes and icons.
 */
object SkillMetadata {
    private val themes = mapOf(
        SkillType.WOODCUTTING to SkillTheme(
            primaryColor = Color(0xFF2E7D32),
            selectedBgColor = Color(0xFFE8F5E9),
            indicatorColor = Color(0xFF4CAF50),
            unselectedIconTint = Color.Gray,
            panelBackgroundColor = Color(0x0A4CAF50),
            iconResId = R.drawable.ic_tree,
            biomeColors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
        ),
        SkillType.MINING to SkillTheme(
            primaryColor = Color(0xFF37474F),
            selectedBgColor = Color(0xFFECEFF1),
            indicatorColor = Color(0xFF455A64),
            unselectedIconTint = Color(0xFF78909C),
            panelBackgroundColor = Color(0x1A37474F),
            iconResId = R.drawable.ic_tree, // TODO: Replace with mining icon
            biomeColors = listOf(Color(0xFF424242), Color(0xFF212121))
        ),
        SkillType.FISHING to SkillTheme(
            primaryColor = Color(0xFF0277BD),
            selectedBgColor = Color(0xFFE1F5FE),
            indicatorColor = Color(0xFF0288D1),
            unselectedIconTint = Color(0xFF81D4FA),
            panelBackgroundColor = Color(0x1A0277BD),
            iconResId = R.drawable.ic_tree, // TODO: Replace with fishing icon
            biomeColors = listOf(Color(0xFF01579B), Color(0xFF0277BD))
        )
    )

    /**
     * Get the visual theme for a given skill.
     *
     * @param skill The skill to get the theme for.
     * @return The [SkillTheme] for the skill, or a default theme if not found.
     */
    fun getTheme(skill: SkillType): SkillTheme {
        return themes[skill] ?: SkillTheme(
            primaryColor = Color.Gray,
            selectedBgColor = Color.LightGray,
            indicatorColor = Color.DarkGray,
            unselectedIconTint = Color.Gray,
            panelBackgroundColor = Color.Transparent,
            iconResId = R.drawable.ic_tree,
            biomeColors = listOf(Color.Gray, Color.DarkGray)
        )
    }

    /**
     * Get the theme for a skill by its name.
     */
    fun getTheme(skillName: String): SkillTheme {
        val skillType = SkillType.fromString(skillName)
        return if (skillType != null) getTheme(skillType) else getTheme(SkillType.WOODCUTTING) // Fallback
    }

    /**
     * Get the icon for a specific training method.
     */
    fun getMethodIcon(skillName: String, methodName: String): Int {
        // Placeholder implementation
        return R.drawable.ic_tree
    }
}
