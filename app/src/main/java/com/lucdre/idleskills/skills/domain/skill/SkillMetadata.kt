package com.lucdre.idleskills.skills.domain.skill

import androidx.compose.ui.graphics.Color
import com.lucdre.idleskills.R

import java.util.Locale

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
        "Woodcutting" to SkillTheme(
            primaryColor = Color(0xFF2E7D32),
            selectedBgColor = Color(0xFFE8F5E9),
            indicatorColor = Color(0xFF4CAF50),
            unselectedIconTint = Color.Gray,
            panelBackgroundColor = Color(0x0A4CAF50),
            iconResId = R.drawable.ic_tree,
            biomeColors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
        ),
        "Mining" to SkillTheme(
            primaryColor = Color(0xFF37474F),
            selectedBgColor = Color(0xFFECEFF1),
            indicatorColor = Color(0xFF455A64),
            unselectedIconTint = Color(0xFF78909C),
            panelBackgroundColor = Color(0x1A37474F),
            iconResId = R.drawable.ic_tree, // TODO: Replace with mining icon
            biomeColors = listOf(Color(0xFF424242), Color(0xFF212121))
        ),
        "Fishing" to SkillTheme(
            primaryColor = Color(0xFF0277BD),
            selectedBgColor = Color(0xFFE1F5FE),
            indicatorColor = Color(0xFF0288D1),
            unselectedIconTint = Color(0xFF81D4FA),
            panelBackgroundColor = Color(0x1A0277BD),
            iconResId = R.drawable.ic_tree, // TODO: Replace with fishing icon
            biomeColors = listOf(Color(0xFF0277BD), Color(0xFF01579B))
        )
    )

    private val defaultTheme = SkillTheme(
        primaryColor = Color.Gray,
        selectedBgColor = Color.LightGray.copy(alpha = 0.2f),
        indicatorColor = Color.Gray,
        unselectedIconTint = Color.Gray,
        panelBackgroundColor = Color.Transparent,
        iconResId = R.drawable.ic_tree,
        biomeColors = listOf(Color(0xFF121212), Color(0xFF000000))
    )

    /**
     * Gets the visual theme for a given skill.
     */
    fun getTheme(skillName: String): SkillTheme {
        return themes[skillName] ?: themes[skillName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }] ?: defaultTheme
    }

    /**
     * Gets the icon resource ID for a specific training method.
     * Fallback to the skill's main icon if no specific mapping exists.
     */
    fun getMethodIcon(skillName: String, methodName: String): Int {
        // This could be expanded into a more complex mapping if needed
        return themes[skillName]?.iconResId ?: defaultTheme.iconResId
    }
}
