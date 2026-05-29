package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.PrestigePoints
import com.lucdre.idleskills.prestige.presentation.PrestigeCard
import com.lucdre.idleskills.prestige.presentation.PrestigeUiState
import com.lucdre.idleskills.prestige.presentation.PrestigeViewModel
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.ExpandableSkillItem
import com.lucdre.idleskills.skills.presentation.SkillListUiState
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.components.OfflineProgressPopup
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Main screen. Displays the list of skills.
 *
 * @param modifier Modifier
 * @param skillViewModel ViewModel that provides UI state and handles UI events for Skills
 * @param prestigeViewModel ViewModel that provides UI state and handles UI events for Prestige
 */
@Composable
fun SkillListScreen(
    modifier: Modifier = Modifier,
    skillViewModel: SkillListViewModel,
    prestigeViewModel: PrestigeViewModel = hiltViewModel()
) {
    val skillUiState by skillViewModel.uiState.collectAsStateWithLifecycle()
    val prestigeUiState by prestigeViewModel.uiState.collectAsStateWithLifecycle()
    val trainingProgress by skillViewModel.trainingProgress.collectAsStateWithLifecycle()
    var showSkillTree by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        SkillListScreenContents(
            modifier = Modifier.fillMaxSize(),
            skillUiState = skillUiState,
            prestigeUiState = prestigeUiState,
            trainingProgress = trainingProgress,
            onSkillClick = { /* Handled by toggle */ },
            onToggleExpand = { skillName ->
                skillViewModel.toggleSkillExpansion(skillName)
            },
            onMethodSelected = { skillViewModel.selectTrainingMethod(it) },
            onPrestigeClick = {
                prestigeViewModel.prestige(
                    resetTrainingState = {
                        skillViewModel.resetTrainingState()
                    }
                )
            },
            onSkillTreeClick = {
                showSkillTree = true
            }
        )

        // Offline Progress Popup
        skillUiState.offlineProgress?.let { result ->
            OfflineProgressPopup(
                result = result,
                onDismiss = { skillViewModel.dismissOfflineProgress() }
            )
        }

        // Skill Tree Screen as overlay
        if (showSkillTree) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                SkillTreeScreen(
                    onClose = { showSkillTree = false }
                )
            }
        }
    }
}

/**
 * Renders different UI based on the current state:
 * - Loading indicator when data is being fetched (needed?)
 * - Error message when there's an error
 * - List of skills when data is available
 *
 * Shows more UI thanks to [com.lucdre.idleskills.skills.presentation.ExpandableSkillItem]
 *
 * @param modifier Modifier
 * @param skillUiState Current UI state of Skills from the ViewModel
 * @param prestigeUiState Current UI state of Prestige from the ViewModel
 * @param trainingProgress To handle XP/level progress bars
 * @param onSkillClick Callback for when a skill is clicked
 * @param onToggleExpand Callback for when a skill's expansion state should toggle
 * @param onMethodSelected Callback for when a training method is selected
 * @param onPrestigeClick Callback for when the prestige button is selected
 * @param onSkillTreeClick Callback for when the skill tree button is selected
 */
@Composable
private fun SkillListScreenContents(
    modifier: Modifier = Modifier,
    skillUiState: SkillListUiState,
    prestigeUiState: PrestigeUiState,
    trainingProgress: Float,
    onSkillClick: (Skill) -> Unit,
    onToggleExpand: (String) -> Unit,
    onMethodSelected: (TrainingMethod) -> Unit,
    onPrestigeClick: () -> Unit,
    onSkillTreeClick: () -> Unit
) {
    val expandedSkillName = skillUiState.expandedSkillName
    Column(modifier = modifier) {
        if (skillUiState.isLoading) {
            // Loading indicator
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (skillUiState.error != null) {
            // Error message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${skillUiState.error}")
            }
        } else {
            // Skill list
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                /* Hide Prestige Card for now, end-game content
                item {
                    PrestigeCard(
                        prestige = prestigeUiState.prestige,
                        isPerformingPrestige = prestigeUiState.isPerformingPrestige,
                        onPrestigeClick = onPrestigeClick,
                        onSkillTreeClick = onSkillTreeClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                */

                // Skill list
                items(skillUiState.skills, key = { it.name }) { skill ->
                    val isActiveSkill = skill.name == skillUiState.activeSkill
                    val isSelectedSkill = skill.name == expandedSkillName

                    ExpandableSkillItem(
                        skill = skill,
                        isActive = isActiveSkill,
                        isExpanded = isSelectedSkill,
                        xpPerHour = if (isActiveSkill) {
                            skillUiState.activeTrainingMethod?.calculateXpPerHour(skillUiState.activeCards)
                                ?: 3600 // Fallback to 3600 (1 XP per second)
                        } else {
                            0
                        },
                        trainingMethods = if (isSelectedSkill) skillUiState.trainingMethods else emptyList(),
                        activeMethod = if (isActiveSkill) skillUiState.activeTrainingMethod else null,
                        activeCards = if (isActiveSkill) skillUiState.activeCards else emptyList(),
                        trainingProgress = if (isActiveSkill) trainingProgress else 0f,
                        onSkillClick = onSkillClick,
                        onToggleExpand = { onToggleExpand(skill.name) },
                        onMethodSelected = onMethodSelected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun SkillListScreenContentsPreview() {
    IdleSkillsTheme {
        val previewSkillState = SkillListUiState(
            skills = listOf(
                Skill("Woodcutting", 10, 1500),
                Skill("Fishing", 20, 4200),
                Skill("Mining", 15, 2800),
                Skill("Cooking", 30, 8100)
            ),
            isLoading = false
        )

        val previewPrestigeState = PrestigeUiState(
            prestige = Prestige(
                points = PrestigePoints(availablePrestigePoints = 1, totalPrestigePoints = 3),
                canPrestige = false
            ),
            isLoading = false,
            isPerformingPrestige = false
        )

        SkillListScreenContents(
            modifier = Modifier.padding(8.dp),
            skillUiState = previewSkillState,
            prestigeUiState = previewPrestigeState,
            trainingProgress = 0.5f,
            onSkillClick = { /* nothing */ },
            onToggleExpand = { /* nothing */ },
            onMethodSelected = { /* nothing */ },
            onPrestigeClick = { /* nothing */ },
            onSkillTreeClick = { /* nothing */ }
        )
    }
}
