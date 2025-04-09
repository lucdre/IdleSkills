package com.lucdre.idleskills.skills.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import com.lucdre.idleskills.skills.domain.skill.Skill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Displays information about a skill.
 *
 * Shows skill name, level, XP progress to next level and XP gain rate
 * when the skill is actively being trained.
 *
 * @param skill The skill to display
 * @param isActive Whether this skill is currently being trained
 * @param xpPerHour Current XP gain rate when active
 * @param onItemClick Callback for when this skill item is clicked
 */
@Composable
fun SkillItem(
    skill: Skill,
    isActive: Boolean = false,
    xpPerHour: Int = 0,
    onItemClick: (Skill) -> Unit
) {
    val xpRequired = LevelCalculator.xpForNextLevel(skill.level)
    val progress = skill.xp.toFloat() / xpRequired.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick(skill) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isActive) Color(0x1A4CAF50) else Color.Transparent)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Level ${skill.level}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // XP Progress bar
            CustomLinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                progress = progress,
                progressColor = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // XP Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "XP: ${skill.xp}/${xpRequired}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isActive) {
                    Text(
                        text = "XP/h: ${xpPerHour}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

/**
 * A custom linear progress indicator. Doesn't clip at the end like the default one.
 *
 * @param modifier Modifier
 * @param progress The current progress, between 0 and 1
 * @param progressColor The color of the progress bar
 * @param backgroundColor The color of the background
 * @param cornerRadius The radius [dp] of the corners
 */
@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    cornerRadius: Dp = 4.dp
) {
    // Ensure progress is between 0 and 1
    val progressValue = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .semantics {
                this.progressBarRangeInfo = ProgressBarRangeInfo(
                    current = progressValue,
                    range = 0f..1f,
                )
            }
    ) {
        // Only show progress bar if there's actual progress
        if (progressValue > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressValue)
                    .background(progressColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillItemPreview() {
    IdleSkillsTheme {
        SkillItem(
            skill = Skill(
                name = "Woodcutting",
                level = 42,
                xp = 5732
            ),
            isActive = false,
            xpPerHour = 0,
            onItemClick = {  }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActiveSkillItemPreview() {
    IdleSkillsTheme {
        SkillItem(
            skill = Skill("Woodcutting", 42, 5732),
            isActive = true,
            xpPerHour = 3600,
            onItemClick = {  }
        )
    }
}
