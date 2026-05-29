package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.util.formatNumber
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen displaying all available stats. Your skill details and unlocked tools.
 *
 * @param modifier Modifier
 * @param skillViewModel ViewModel that provides all skills data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    skillViewModel: SkillListViewModel = hiltViewModel()
) {
    val skillUiState by skillViewModel.uiState.collectAsStateWithLifecycle()

    // Bottom Sheet State for Skill onClick
    var selectedSkillName by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }

    // Get live skill data from the ViewModel state
    val selectedSkill = selectedSkillName?.let { name ->
        skillUiState.skills.find { it.name == name }
    }

    Box(modifier = modifier.fillMaxSize()) {
        StatsScreenContent(
            modifier = Modifier.fillMaxSize(),
            playerStatistics = skillUiState.playerStatistics,
            skills = skillUiState.skills
        ) { skill ->
            selectedSkillName = skill.name //it is read
            isSheetOpen = true
        }

        if (isSheetOpen && selectedSkill != null) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
                containerColor = Color(0xFF2D2D2D),
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
            ) {
                SkillDetailSheetContent(skill = selectedSkill)
            }
        }
    }
}

/**
 * Content for the Stats screen.
 *
 * @param modifier Modifier
 * @param skills List of all skills to display
 */
@Composable
private fun StatsScreenContent(
    modifier: Modifier = Modifier,
    playerStatistics: PlayerStatistics,
    skills: List<Skill>,
    onSkillClick: (Skill) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Stats",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Player Statistics",
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
            }
        }

        // Skills section
        Text(
            text = "Skills",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            StatsSkillsScreen(
                modifier = Modifier.fillMaxSize(),
                skills = skills,
                onSkillSelect = onSkillClick
            )
        }
    }
}

@Composable
fun SkillDetailSheetContent(skill: Skill) {
    val xpToNextLevel = LevelCalculator.xpToNextLevelFromTotal(skill.xp, skill.level)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp), // Extra padding for navigation bar
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = skill.name,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DetailItem(
                label = "Level",
                value = skill.level.toString()
            )
            DetailItem(
                label = "Total XP",
                value = skill.xp.formatNumber()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DetailItem(
            label = "XP remaining",
            value = xpToNextLevel.formatNumber()
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = value,
            color = Color.Yellow,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    IdleSkillsTheme {
        val previewSkills = listOf(
            Skill("Woodcutting", level = 10, xp = 1500),
            Skill("Mining", level = 15, xp = 2800),
            Skill("Fishing", level = 20, xp = 4200),
            Skill("Firemaking", level = 1, xp = 0),
            Skill("Cooking", level = 30, xp = 8100)
        )
        val previewStatistics = PlayerStatistics(
            stats = mapOf(
                "Woodcutting" to mapOf("Oak" to 42),
                "Mining" to mapOf("Iron" to 15),
                "Fishing" to mapOf("Shrimp" to 100)
            )
        )
        StatsScreenContent(
            modifier = Modifier.fillMaxSize(),
            playerStatistics = previewStatistics,
            skills = previewSkills,
            onSkillClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2D2D2D)
@Composable
fun SkillDetailSheetPreview() {
    IdleSkillsTheme {
        SkillDetailSheetContent(
            skill = Skill("Woodcutting", level = 42, xp = 4500)
        )
    }
}
