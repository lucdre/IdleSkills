package com.lucdre.idleskills.prestige.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.PrestigePoints
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeProgress
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Card component displaying prestige information and prestige button.
 *
 * @param modifier Modifier for styling
 * @param prestige Current prestige state
 * @param isPerformingPrestige Whether a prestige operation is in progress
 * @param onPrestigeClick Callback when prestige button is clicked
 * @param onSkillTreeClick Callback when skill tree button is clicked
 */
@Composable
fun PrestigeCard(
    modifier: Modifier = Modifier,
    prestige: Prestige,
    isPerformingPrestige: Boolean,
    onPrestigeClick: () -> Unit,
    onSkillTreeClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prestige points display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Prestige Points",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Available: ${prestige.points.availablePrestigePoints}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = "Total:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = "${prestige.points.totalPrestigePoints}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Requirement text
            Text(
                text = if (prestige.canPrestige) {
                    "Ready to prestige! You'll earn points to spend in the skill tree." // TODO specify how manu points
                } else {
                    "Reach level 99 in any unlocked skill to earn prestige points."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Prestige button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPrestigeClick,
                enabled = prestige.canPrestige && !isPerformingPrestige
            ) {
                if (isPerformingPrestige) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = when {
                        isPerformingPrestige -> "Prestiging..."
                        prestige.canPrestige -> "Prestige & Earn Points!"
                        else -> "Level a skill to 99"
                    }
                )
            }

            // Skill Tree button (show if player has points to spend)
            if (prestige.points.availablePrestigePoints > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSkillTreeClick
                ) {
                    Text("Open Skill Tree")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrestigeCardPreview() {
    IdleSkillsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Can prestige
            PrestigeCard(
                prestige = Prestige(
                    points = PrestigePoints(availablePrestigePoints = 2, totalPrestigePoints = 5),
                    canPrestige = true
                ),
                isPerformingPrestige = false,
                onPrestigeClick = { },
                onSkillTreeClick = {}
            )

            // Cannot prestige
            PrestigeCard(
                prestige = Prestige(
                    points = PrestigePoints(availablePrestigePoints = 0, totalPrestigePoints = 3),
                    canPrestige = false
                ),
                isPerformingPrestige = false,
                onPrestigeClick = { },
                onSkillTreeClick = {}
            )

            // Fresh start
            PrestigeCard(
                prestige = Prestige(
                    skillTreeProgress = SkillTreeProgress()
                ),
                isPerformingPrestige = false,
                onPrestigeClick = { },
                onSkillTreeClick = {}
            )
        }
    }
}
