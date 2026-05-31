package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

@Composable
fun TrainingMethodCard(
    modifier: Modifier = Modifier,
    method: TrainingMethod,
    selected: Boolean,
    onClick: () -> Unit
) {
    val theme = SkillMetadata.getTheme(method.skill)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                theme.primaryColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        ),
        border = if (selected) {
            BorderStroke(2.dp, theme.primaryColor)
        } else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SkillMetadata.getMethodIcon(method.skill.name, method.name)),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (selected) theme.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) theme.primaryColor else MaterialTheme.colorScheme.onSurface
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${method.xpPerAction} XP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) theme.primaryColor else MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (selected) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = theme.primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TrainingMethodCardPreview() {
    IdleSkillsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TrainingMethodCard(
                method = TrainingMethod(
                    skill = SkillType.WOODCUTTING,
                    name = "Oak Trees",
                    xpPerAction = 37,
                    actionDurationMs = 4000
                ),
                selected = true,
                onClick = {}
            )
        }
    }
}
