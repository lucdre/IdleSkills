package com.lucdre.idleskills.ui.screens

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
 * @param viewModel ViewModel that provides UI state and handles UI events
 */
@Composable
fun SkillListScreen(modifier: Modifier = Modifier, viewModel: SkillListViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedSkillName by remember { mutableStateOf<String?>(null) }

    // Fetch the data when the composable is created
    LaunchedEffect(key1 = true){
        viewModel.loadSkills()
    }

    Box(modifier = modifier.fillMaxSize()) {
        SkillListScreenContents(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            expandedSkillName = expandedSkillName,
            onSkillClick = { viewModel.onSkillClick(it) },
            onToggleExpand = { skillName -> 
                expandedSkillName = if (expandedSkillName == skillName) null else skillName
            },
            onMethodSelected = { viewModel.selectTrainingMethod(it) },
            onToolSelected = { uiState.activeSkill?.let { viewModel.selectBetterTool(it) } }
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
 * @param uiState Current UI state from the ViewModel
 * @param expandedSkillName Name of the currently expanded skill, if any
 * @param onSkillClick Callback for when a skill is clicked
 * @param onToggleExpand Callback for when a skill's expansion state should toggle
 * @param onMethodSelected Callback for when a training method is selected
 * @param onToolSelected Callback for when a better tool is selected
 */
@Composable
private fun SkillListScreenContents(
    modifier: Modifier = Modifier,
    uiState: SkillListUiState,
    expandedSkillName: String?,
    onSkillClick: (Skill) -> Unit,
    onToggleExpand: (String) -> Unit,
    onMethodSelected: (TrainingMethod) -> Unit,
    onToolSelected: () -> Unit
) {
    Column(modifier = modifier) {
        if (uiState.isLoading) {
            // Loading indicator
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            // Error message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}")
            }
        } else {
            // Skill list
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.skills) { skill ->
                    val isActiveSkill = skill.name == uiState.activeSkill

                    ExpandableSkillItem(
                        skill = skill,
                        isActive = isActiveSkill,
                        isExpanded = skill.name == expandedSkillName,
                        xpPerHour = if (isActiveSkill)
                            uiState.activeTrainingMethod?.calculateXpPerHour()
                                ?: 3600 // Fallback to 3600 (1 XP per second)
                        else 0,
                        trainingMethods = if (isActiveSkill) uiState.trainingMethods else emptyList(),
                        activeMethod = if (isActiveSkill) uiState.activeTrainingMethod else null,
                        activeTool = if (isActiveSkill) uiState.activeTool else null,
                        hasBetterToolAvailable = if (isActiveSkill) uiState.hasBetterToolAvailable else false,
                        trainingProgress = if (isActiveSkill) uiState.trainingProgress else 0f,
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
        val previewState = SkillListUiState(
            skills = listOf(
                Skill("Woodcutting", 10, 1500),
                Skill("Fishing", 20, 4200),
                Skill("Mining", 15, 2800),
                Skill("Cooking", 30, 8100)
            ),
            isLoading = false
        )

        var expandedSkillName by remember { mutableStateOf<String?>("Woodcutting") }

        SkillListScreenContents(
            modifier = Modifier.padding(8.dp),
            uiState = previewState,
            expandedSkillName = expandedSkillName,
            onSkillClick = { /* nothing */ },
            onToggleExpand = { name -> expandedSkillName = if (expandedSkillName == name) null else name },
            onMethodSelected = { /* nothing */ },
            onToolSelected = { /* nothing */ }
        )
    }
}