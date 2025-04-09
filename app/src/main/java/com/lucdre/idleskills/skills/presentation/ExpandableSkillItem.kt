package com.lucdre.idleskills.skills.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.util.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Expandable component displaying information about a skill.
 *
 * Shows skill name, level, XP progress to next level, and optionally XP gain rate
 * when the skill is actively being trained. When expanded, it shows training methods
 * directly within the card.
 *
 * @param skill The skill to display
 * @param isActive Whether this skill is currently being trained
 * @param isExpanded Whether this skill item is currently expanded
 * @param xpPerHour Current XP gain rate when active
 * @param trainingMethods List of available training methods for this skill
 * @param activeMethod Currently selected training method
 * @param activeTool Currently selected tool
 * @param trainingProgress Progress of the current training action (0-1)
 * @param onSkillClick Callback for when this skill is clicked
 * @param onToggleExpand Callback for when expansion state should change
 * @param onMethodSelected Callback for when a training method is selected
 */
@Composable
fun ExpandableSkillItem(
    skill: Skill,
    isActive: Boolean = false,
    isExpanded: Boolean,
    xpPerHour: Int = 0,
    trainingMethods: List<TrainingMethod> = emptyList(),
    activeMethod: TrainingMethod? = null,
    activeTool: Tool? = null,
    trainingProgress: Float = 0f,
    onSkillClick: (Skill) -> Unit,
    onToggleExpand: () -> Unit,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    val xpRequired = LevelCalculator.xpForNextLevel(skill.level)
    val progress = skill.xp.toFloat() / xpRequired.toFloat()
    val rotationState by animateFloatAsState(if (isExpanded) 180f else 0f, label = "rotate")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isActive) Color(0x1A4CAF50) else Color.Transparent)
        ) {
            // Main skill row (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        if (!isExpanded) {
                            // Only trigger the skill click when collapsing to avoid double click issues
                            onSkillClick(skill) 
                        }
                        onToggleExpand()
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skill info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Skill name
                        Text(
                            text = skill.name,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Skill level
                            Text(
                                text = "Level ${skill.level}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Expansion toggle
                            IconButton(onClick = { 
                                if (!isExpanded) {
                                    // Only trigger the skill click when collapsing to avoid double click issues
                                    onSkillClick(skill) 
                                }
                                onToggleExpand() 
                            }) {
                                Icon(
                                    modifier = Modifier.rotate(rotationState),
                                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // XP Progress bar
                    CustomLinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        progress = progress,
                        progressColor = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // XP Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP: ${skill.xp}/${xpRequired}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (isActive) {
                            Text(
                                text = "XP/h: ${xpPerHour}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }

            
            // Expanded content with training methods and extra info
            AnimatedVisibility(
                modifier = Modifier.wrapContentHeight(unbounded = false),
                visible = isExpanded,
                enter = expandVertically(initialHeight = { 0 }),
                exit = shrinkVertically(targetHeight = { 0 }),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .wrapContentHeight()
                ) {

                    // Content box
                    if (trainingMethods.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .height(240.dp)
                        ) {
                            TrainingMethodsPanelFactory.CreateTrainingMethodsPanel(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent),
                                skillName = skill.name,
                                methods = trainingMethods,
                                activeMethod = activeMethod,
                                trainingProgress = trainingProgress,
                                activeTool = activeTool,
                                onMethodSelected = onMethodSelected
                            )
                        }
                    } else {
                        Text(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = "No training methods available yet",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandableSkillItemCollapsedPreview() {
    IdleSkillsTheme {
        ExpandableSkillItem(
            skill = Skill("Woodcutting", 42, 5732),
            isActive = false,
            isExpanded = false,
            xpPerHour = 0,
            onSkillClick = { },
            onToggleExpand = { },
            onMethodSelected = { }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ExpandableSkillItemExpandedPreview() {
    val methods = listOf(
        TrainingMethod("Woodcutting", "Tree", 10, 10000),
        TrainingMethod("Woodcutting", "Oak Tree", 15, 10000, 5),
        TrainingMethod("Woodcutting", "Willow Tree", 30, 15000, 20)
    )
    
    IdleSkillsTheme {
        ExpandableSkillItem(
            skill = Skill("Woodcutting", 42, 5732),
            isActive = true,
            isExpanded = true,
            xpPerHour = 3600,
            trainingMethods = methods,
            activeMethod = methods[1],
            activeTool = Tool("Woodcutting", "Iron Axe", 1.2f, 5, null),
            trainingProgress = 0.35f,
            onSkillClick = { },
            onToggleExpand = { },
            onMethodSelected = { }
        )
    }
}