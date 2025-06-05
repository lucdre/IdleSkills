package com.lucdre.idleskills.prestige.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.PrestigeConfig
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Card component displaying prestige information and prestige button.
 *
 * @param prestige Current prestige state
 * @param isPerformingPrestige Whether a prestige operation is in progress
 * @param onPrestigeClick Callback when prestige button is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun PrestigeCard(
    prestige: Prestige,
    isPerformingPrestige: Boolean,
    onPrestigeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prestige level display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prestige Level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${prestige.level}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Requirement text
            val requiredSkills = PrestigeConfig.getRequiredSkills(prestige.level)
            val requiredLevel = PrestigeConfig.getRequiredLevel(prestige.level)

            if (requiredSkills.isNotEmpty()) {
                Text(
                    text = "Requirements: All ${requiredSkills.size} skills at level $requiredLevel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "No further prestiges available yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                        prestige.canPrestige -> "Prestige!"
                        requiredSkills.isNotEmpty() -> "Requirements not met"
                        else -> "Max prestige reached!"
                    }
                )
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
                prestige = Prestige(level = 0, canPrestige = true),
                isPerformingPrestige = false,
                onPrestigeClick = { }
            )

            // Cannot prestige
            PrestigeCard(
                prestige = Prestige(level = 0, canPrestige = false),
                isPerformingPrestige = false,
                onPrestigeClick = { }
            )

            // After first prestige
            PrestigeCard(
                prestige = Prestige(level = 1, canPrestige = false),
                isPerformingPrestige = false,
                onPrestigeClick = { }
            )
        }
    }
}