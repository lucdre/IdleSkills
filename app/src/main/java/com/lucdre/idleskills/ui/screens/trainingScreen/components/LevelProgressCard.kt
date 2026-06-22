package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.ui.components.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.NumberFormatter

@Composable
fun LevelProgressCard(
    modifier: Modifier = Modifier,
    level: Int,
    totalXp: Int,
    nextLevelXp: Int,
    xpToNextLevel: Int,
    progress: Float
) {
    // Smoothly interpolate XP gain and level ups
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (progress == 0f) snap() else tween(durationMillis = 300, easing = LinearEasing),
        label = "xpProgress"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (level >= LevelCalculator.MAX_LEVEL) "XP to Cap" else "XP to Next Level",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = NumberFormatter.formatNumber(xpToNextLevel),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        CustomLinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            progressColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Level: $level",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${NumberFormatter.formatNumber(totalXp)} / ${NumberFormatter.formatNumber(nextLevelXp)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun LevelProgressCardPreview() {
    IdleSkillsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LevelProgressCard(
                level = 10,
                totalXp = 10000,
                nextLevelXp = 28000,
                xpToNextLevel = 2655,
                progress = 0.85f
            )
        }
    }
}
