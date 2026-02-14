package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Displays skill levels on [StatsScreen]
 *
 * @param modifier Modifier
 * @param skills List of skills to display
 */
@Composable
fun StatsSkillsScreen(
    modifier: Modifier = Modifier,
    skills: List<Skill>,
    onSkillSelect: (Skill) -> Unit
) {
    StatsSkillsContent(
        modifier = modifier,
        skills = skills,
        onSkillSelect = onSkillSelect
    )
}

/**
 * Content displaying skills in a grid with icons and levels.
 *
 * @param modifier Modifier
 * @param skills List of skills to display
 */
@Composable
fun StatsSkillsContent(
    modifier: Modifier = Modifier,
    skills: List<Skill>,
    onSkillSelect: (Skill) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = skills,
            key = { it.name }
        ) { skill ->
            SkillItem(
                name = skill.name,
                level = skill.level,
                iconResId = R.drawable.ic_tree, // todo fix this for when there are more
                onClick = { onSkillSelect(skill) }
            )
        }
    }
}

/**
 * A single skill item displaying icon with level overlay and skill name.
 *
 * @param name Skill name
 * @param level Current skill level
 * @param iconResId Resource ID for the skill icon
 */
@Composable
fun SkillItem(
    modifier: Modifier = Modifier,
    name: String,
    level: Int,
    iconResId: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFF4A4A4A), RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color(0xFF5A5A5A)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = name,
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
        }

        // Level
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color(0xFF6A6A6A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.toString(),
                color = Color.Yellow,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsSkillsScreenPreview() {
    IdleSkillsTheme {
        val previewSkills = listOf(
            Skill("Woodcutting", level = 10, xp = 1500),
            Skill("Mining", level = 15, xp = 2800),
            Skill("Fishing", level = 20, xp = 4200),
            Skill("Smelting", level = 5, xp = 500),
            Skill("Cooking", level = 30, xp = 8100),
            Skill("Smithing", level = 1, xp = 0)
        )
        StatsSkillsScreen(
            modifier = Modifier.fillMaxSize(),
            skills = previewSkills,
            onSkillSelect = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 100)
@Composable
fun SkillItemPreview() {
    IdleSkillsTheme {
        SkillItem(
            name = "Woodcutting",
            level = 42,
            iconResId = R.drawable.ic_tree,
            onClick = {}
        )
    }
}
