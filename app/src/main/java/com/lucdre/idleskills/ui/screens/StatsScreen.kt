package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen displaying all available stats. Your skill details and unlocked tools.
 *
 * @param modifier Modifier
 * @param skillViewModel ViewModel that provides all skills data
 */
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    skillViewModel: SkillListViewModel = hiltViewModel()
) {
    val skillUiState by skillViewModel.uiState.collectAsStateWithLifecycle()

    StatsScreenContent(
        modifier = modifier,
        skills = skillUiState.skills
    )
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
    skills: List<Skill>
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Stats",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                skills = skills
            )
        }
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
        StatsScreenContent(
            modifier = Modifier.fillMaxSize(),
            skills = previewSkills
        )
    }
}
