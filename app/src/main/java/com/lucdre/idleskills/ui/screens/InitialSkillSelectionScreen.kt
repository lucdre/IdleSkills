package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucdre.idleskills.prestige.domain.InitialSkillConfig
import com.lucdre.idleskills.prestige.presentation.InitialSkillSelectionViewModel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen for selecting the initial skill when starting a fresh game.
 *
 * @param modifier Modifier for styling
 * @param onSkillSelected Callback when a skill is successfully selected
 * @param viewModel ViewModel handling the skill selection logic
 */
@Composable
fun InitialSkillSelectionScreen(
    modifier: Modifier = Modifier,
    onSkillSelected: () -> Unit,
    viewModel: InitialSkillSelectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSkillSelected) {
        if (uiState.isSkillSelected) {
            onSkillSelected()
        }
    }

    InitialSkillSelectionContent(
        modifier = modifier,
        availableSkills = InitialSkillConfig.availableStartingSkills,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onSkillClick = { skillName ->
            viewModel.selectSkill(skillName)
        }
    )
}

/**
 * Content for the initial skill selection screen.
 */
@Composable
private fun InitialSkillSelectionContent(
    modifier: Modifier = Modifier,
    availableSkills: List<String>,
    isLoading: Boolean,
    errorMessage: String?,
    onSkillClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome header
        Text(
            text = "Welcome to Idle Skills!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose your starting skill to begin your journey:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Skill selection buttons
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableSkills) { skillName ->
                SkillSelectionCard(
                    skillName = skillName,
                    isEnabled = !isLoading,
                    onClick = { onSkillClick(skillName) }
                )
            }
        }
    }
}

/**
 * Card for selecting a skill.
 */
@Composable
private fun SkillSelectionCard(
    modifier: Modifier = Modifier,
    skillName: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = isEnabled,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = skillName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getSkillDescription(skillName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Get description for each skill.
 */
private fun getSkillDescription(skillName: String): String {
    return when (skillName) {
        "Woodcutting" -> "WC_PH_DESC"
        "Mining" -> "MINING_PH_DESC"
        "Fishing" -> "FISHING_PH_DESC"
        else -> "ELSE_PH_DESC"
    }
}

@Preview(showBackground = true)
@Composable
fun InitialSkillSelectionScreenPreview() {
    IdleSkillsTheme {
        InitialSkillSelectionContent(
            availableSkills = InitialSkillConfig.availableStartingSkills,
            isLoading = false,
            errorMessage = null,
            onSkillClick = { }
        )
    }
}
