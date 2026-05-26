package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.formatNumber

/**
 * Screen displaying settings and player profile.
 *
 * @param modifier Modifier
 * @param skillViewModel Shared ViewModel to access profile data
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    skillViewModel: SkillListViewModel = hiltViewModel()
) {
    val uiState by skillViewModel.uiState.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    SettingsScreenContent(
        modifier = modifier,
        playerProfile = uiState.playerProfile,
        playerStatistics = uiState.playerStatistics,
        onResetClick = { showResetDialog = true }
    )

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?") },
            text = { Text("This will revert everything. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        skillViewModel.resetAllData(context)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("RESET EVERYTHING")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    IdleSkillsTheme {
        SettingsScreenContent(
            playerProfile = PlayerProfile(
                username = "IdleMaster",
                favoriteSkill = "Woodcutting",
                currentRegion = Region.FIRST_REGION
            ),
            playerStatistics = PlayerStatistics(),
            onResetClick = {}
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    playerProfile: PlayerProfile,
    playerStatistics: PlayerStatistics,
    onResetClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Player Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Player Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Username", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = playerProfile.username.ifBlank { "Anonymous" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Current Region", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = playerProfile.currentRegion.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Favorite Skill", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = playerProfile.favoriteSkill,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Trees cut", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = playerStatistics.getCountForSkill("Woodcutting").formatNumber(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Rocks mined", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = playerStatistics.getCountForSkill("Mining").formatNumber(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Fish fished", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = playerStatistics.getCountForSkill("Fishing").formatNumber(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onResetClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
        ) {
            Text("RESET ALL PROGRESS")
        }
    }
}
