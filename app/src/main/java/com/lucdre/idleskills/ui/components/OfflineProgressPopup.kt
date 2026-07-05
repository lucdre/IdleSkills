package com.lucdre.idleskills.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.core.domain.OfflineProgressResult
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.LocalItemRegistry
import com.lucdre.idleskills.ui.util.formatNumber

/**
 * Offline gains popup.
 */
@Composable
fun OfflineProgressPopup(
    result: OfflineProgressResult,
    onDismiss: () -> Unit
) {
    val itemRegistry = LocalItemRegistry.current
    val skillTheme = SkillMetadata.getTheme(result.skillName)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time elapsed
                val totalSeconds = result.elapsedMs / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                
                Text(
                    text = "Away for ${hours}h ${minutes}m ${seconds}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Rewards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // XP Gain
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = skillTheme.iconResId),
                            contentDescription = "Skill Icon",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+${result.earnedXp.formatNumber()} XP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Item Gain
                    result.earnedItems.forEach { (itemType, quantity) ->
                        val metadata = itemRegistry.getMetadata(itemType)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = metadata.iconResId),
                                contentDescription = "Item Icon",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "x${quantity.formatNumber()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Ok")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun OfflineProgressPopupPreview() {
    val itemRegistry = com.lucdre.idleskills.inventory.domain.ItemRegistry()
    androidx.compose.runtime.CompositionLocalProvider(LocalItemRegistry provides itemRegistry) {
        IdleSkillsTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                OfflineProgressPopup(
                    result = OfflineProgressResult(
                        skillName = "Woodcutting",
                        earnedXp = 1250,
                        elapsedMs = 3661000L, // 1h 1m 1s
                        earnedItems = mapOf(ItemType.NORMAL_LOGS to 50)
                    ),
                    onDismiss = {}
                )
            }
        }
    }
}
