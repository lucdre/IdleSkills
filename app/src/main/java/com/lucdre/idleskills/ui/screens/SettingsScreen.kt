package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

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

    SettingsScreenContent(
        modifier = modifier,
        playerProfile = uiState.playerProfile
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    IdleSkillsTheme {
        SettingsScreenContent(
            playerProfile = PlayerProfile(
                username = "IdleMaster",
                currentRegion = Region.FIRST_REGION
            )
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    playerProfile: PlayerProfile
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
            }
        }
    }
}
