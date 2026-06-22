package com.lucdre.idleskills.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.core.domain.OfflineProgressResult
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.formatNumber

/**
 * Dialog showing offline progress to the user.
 */
@Composable
fun OfflineProgressPopup(
    result: OfflineProgressResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "While you were away...",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = result.skillName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (result.earnedXp > 0) {
                            Text(
                                text = "+${result.earnedXp.formatNumber()} XP",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Black
                            )
                        } else {
                            Text(
                                text = "XP Cap Reached",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val hours = result.elapsedMs / (1000 * 60 * 60)
                val minutes = (result.elapsedMs / (1000 * 60)) % 60
                
                Text(
                    text = "Time elapsed: ${hours}h ${minutes}m",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Ok")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun OfflineProgressPopupPreview() {
    IdleSkillsTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            OfflineProgressPopup(
                result = OfflineProgressResult(
                    skillName = "Woodcutting",
                    earnedXp = 1250,
                    elapsedMs = 3600000L // 1 hour
                ),
                onDismiss = {}
            )
        }
    }
}
