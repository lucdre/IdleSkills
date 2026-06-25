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
import com.lucdre.idleskills.profile.presentation.SettingsViewModel
import com.jakewharton.processphoenix.ProcessPhoenix
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen displaying settings and player profile.
 *
 * @param modifier Modifier
 * @param viewModel ViewModel
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsViewModel.Effect.TriggerRebirth -> {
                    ProcessPhoenix.triggerRebirth(context)
                }
            }
        }
    }

    SettingsScreenContent(
        modifier = modifier,
        playerProfile = uiState.playerProfile,
        regionName = uiState.currentRegionName,
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
                        viewModel.resetAllData()
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
                username = "IdleMaster"
            ),
            regionName = "Region 1",
            onResetClick = {}
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    playerProfile: PlayerProfile,
    regionName: String,
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
                        text = regionName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

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
