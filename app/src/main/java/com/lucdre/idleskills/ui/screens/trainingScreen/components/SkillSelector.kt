package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.lucdre.idleskills.ui.util.FontScalePreviews
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.ui.components.AutoSizeText
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

@Composable
fun SkillSelector(
    modifier: Modifier = Modifier,
    skills: List<SkillType>,
    selectedSkill: SkillType?,
    onSkillSelected: (SkillType) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = "Select Skill",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(skills) { skill ->
                val isSelected = skill == selectedSkill
                val theme = SkillMetadata.getTheme(skill)
                
                Card(
                    modifier = Modifier
                        .width(110.dp)
                        .clickable { onSkillSelected(skill) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            theme.primaryColor.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        }
                    ),
                    border = if (isSelected) {
                        BorderStroke(2.dp, theme.primaryColor)
                    } else null
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = theme.iconResId),
                            contentDescription = skill.displayName,
                            modifier = Modifier.size(32.dp),
                            tint = if (isSelected) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AutoSizeText(
                            text = skill.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) theme.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            minFontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

@IdleSkillsPreviews
@FontScalePreviews
@Composable
fun SkillSelectorPreview() {
    IdleSkillsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SkillSelector(
                skills = SkillType.entries,
                selectedSkill = SkillType.WOODCUTTING,
                onSkillSelected = {}
            )
        }
    }
}
