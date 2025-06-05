package com.lucdre.idleskills.ui.screens

import androidx.hilt.navigation.compose.hiltViewModel
import com.lucdre.idleskills.prestige.presentation.PrestigeCard
import com.lucdre.idleskills.prestige.presentation.PrestigeViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.presentation.PrestigeUiState
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.ExpandableSkillItem
import com.lucdre.idleskills.skills.presentation.SkillListUiState
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
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
    val skillUiState by skillViewModel.uiState.collectAsState()
    val prestigeUiState by prestigeViewModel.uiState.collectAsState()
    var expandedSkillName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        skillViewModel.loadSkills()
    }

    Box(modifier = modifier.fillMaxSize()) {
        SkillListScreenContents(
            modifier = Modifier.fillMaxSize(),
            skillUiState = skillUiState,
            prestigeUiState = prestigeUiState,
            expandedSkillName = expandedSkillName,
            onSkillClick = { skillViewModel.onSkillClick(it) },
            onToggleExpand = { skillName ->
                expandedSkillName = if (expandedSkillName == skillName) null else skillName
            },
            onMethodSelected = { skillViewModel.selectTrainingMethod(it) },
            onToolSelected = { skillUiState.activeSkill?.let { skillViewModel.selectBetterTool(it) } },
            onPrestigeClick = {
                prestigeViewModel.prestige(
                    resetTrainingState = {
                        skillViewModel.resetTrainingState()
                        expandedSkillName = null
                    }
                )
            }
        )
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
 * @param expandedSkillName Name of the currently expanded skill, if any
 * @param onSkillClick Callback for when a skill is clicked
 * @param onToggleExpand Callback for when a skill's expansion state should toggle
 * @param onMethodSelected Callback for when a training method is selected
 * @param onToolSelected Callback for when a better tool is selected
 * @param onPrestigeClick Callback for when the prestige button is selected
 */
@Composable
private fun SkillListScreenContents(
    modifier: Modifier = Modifier,
    skillUiState: SkillListUiState,
    prestigeUiState: PrestigeUiState,
    expandedSkillName: String?,
    onSkillClick: (Skill) -> Unit,
    onToggleExpand: (String) -> Unit,
    onMethodSelected: (TrainingMethod) -> Unit,
    onToolSelected: () -> Unit,
    onPrestigeClick: () -> Unit
) {
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
                // Prestige Card
                item {
                    PrestigeCard(
                        prestige = prestigeUiState.prestige,
                        isPerformingPrestige = prestigeUiState.isPerformingPrestige,
                        onPrestigeClick = onPrestigeClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }


                // Skill list
                items(skillUiState.skills) { skill ->
                    val isActiveSkill = skill.name == skillUiState.activeSkill

                    ExpandableSkillItem(
                        skill = skill,
                        isActive = isActiveSkill,
                        isExpanded = skill.name == expandedSkillName,
                        xpPerHour = if (isActiveSkill)
                            skillUiState.activeTrainingMethod?.calculateXpPerHour()
                                ?: 3600 // Fallback to 3600 (1 XP per second)
                        else 0,
                        trainingMethods = if (isActiveSkill) skillUiState.trainingMethods else emptyList(),
                        activeMethod = if (isActiveSkill) skillUiState.activeTrainingMethod else null,
                        activeTool = if (isActiveSkill) skillUiState.activeTool else null,
                        hasBetterToolAvailable = if (isActiveSkill) skillUiState.hasBetterToolAvailable else false,
                        trainingProgress = if (isActiveSkill) skillUiState.trainingProgress else 0f,
                        onSkillClick = onSkillClick,
                        onToggleExpand = { onToggleExpand(skill.name) },
                        onMethodSelected = onMethodSelected,
                        onToolSelected = onToolSelected
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
            prestige = Prestige(level = 0, canPrestige = false),
            isLoading = false,
            isPerformingPrestige = false
        )

        var expandedSkillName by remember { mutableStateOf<String?>("Woodcutting") }

        SkillListScreenContents(
            modifier = Modifier.padding(8.dp),
            skillUiState = previewSkillState,
            prestigeUiState = previewPrestigeState,
            expandedSkillName = expandedSkillName,
            onSkillClick = { /* nothing */ },
            onToggleExpand = { name -> expandedSkillName = if (expandedSkillName == name) null else name },
            onMethodSelected = { /* nothing */ },
            onToolSelected = { /* nothing */ },
            onPrestigeClick = { /* nothing */ }
        )
    }
}