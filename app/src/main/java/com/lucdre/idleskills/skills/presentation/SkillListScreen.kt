package com.lucdre.idleskills.skills.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.Skill
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

@Composable
fun SkillListScreen(viewModel: SkillListViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    // Fetch the data when the composable is created
    LaunchedEffect(key1 = true){
        viewModel.loadSkills()
    }

    Box(modifier = modifier.fillMaxSize()) {
        SkillListScreenContents(
            uiState = uiState,
            modifier = Modifier.fillMaxSize(),
            onSkillClick = { viewModel.onSkillClick(it) }
        )

        // Training methods panel at the bottom
        AnimatedVisibility(
            visible = uiState.activeSkill != null && uiState.trainingMethods.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            uiState.activeSkill?.let { activeSkill ->
                TrainingMethodsPanelFactory.CreateTrainingMethodsPanel(
                    skillName = activeSkill,
                    methods = uiState.trainingMethods,
                    activeMethod = uiState.activeTrainingMethod,
                    trainingProgress = uiState.trainingProgress,
                    onMethodSelected = { viewModel.selectTrainingMethod(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        }
    }
}

@Composable
private fun SkillListScreenContents(
    uiState: SkillListUiState,
    modifier: Modifier = Modifier,
    onSkillClick: (Skill) -> Unit
) {
    Column(modifier = modifier) {
        if (uiState.isLoading) {
            // Loading indicator
            Text("Loading...")
        } else if (uiState.error != null) {
            // Error message
            Text("Error: ${uiState.error}")
        } else {
            // Skill list
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(uiState.skills) { skill ->
                    SkillItem(
                        skill = skill,
                        isActive = skill.name == uiState.activeSkill,
                        xpPerHour = if (skill.name == uiState.activeSkill)
                            uiState.activeTrainingMethod?.calculateXpPerHour() ?: 3600 // Fallback to 3600 (1 XP per second)
                        else 0,
                        onItemClick = onSkillClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Preview function
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

        SkillListScreenContents(
            uiState = previewState,
            modifier = Modifier.padding(8.dp),
            onSkillClick = { /* Do nothing in preview */ }
        )
    }
}


