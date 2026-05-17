package com.lucdre.idleskills.skills.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.presentation.util.CustomLinearProgressIndicator
import com.lucdre.idleskills.skills.presentation.util.formatNumber

/**
 * A reusable component that displays a skill's XP progress and levels.
 *
 * @param level Current skill level.
 * @param xp Current skill XP.
 * @param isActive Whether the skill is currently being trained.
 * @param xpPerHour XP per hour rate, shown when active.
 * @param modifier Modifier for the root layout.
 * @param activeColor Color to use for the progress bar when active.
 */
@Composable
fun SkillXpBar(
    level: Int,
    xp: Int,
    isActive: Boolean,
    xpPerHour: Int = 0,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF4CAF50)
) {
    val totalXpForCurrentLevel = LevelCalculator.totalXpForLevel(level)
    val xpIntoCurrentLevel = xp - totalXpForCurrentLevel
    val xpRequiredForNextLevel = LevelCalculator.xpForNextLevel(level)
    val progress = (xpIntoCurrentLevel.toFloat() / xpRequiredForNextLevel.toFloat()).coerceIn(0f, 1f)
    val totalXpForNextLevel = LevelCalculator.totalXpForLevel(level + 1)

    Column(modifier = modifier) {
        CustomLinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            progress = progress,
            progressColor = if (isActive) activeColor else MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "XP: ${xp.formatNumber()}/${totalXpForNextLevel.formatNumber()}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (isActive && xpPerHour > 0) {
                Text(
                    text = "XP/h: ${xpPerHour.formatNumber()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = activeColor
                )
            }
        }
    }
}
