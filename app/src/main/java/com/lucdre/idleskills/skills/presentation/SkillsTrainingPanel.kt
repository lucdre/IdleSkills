package com.lucdre.idleskills.skills.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.util.CustomLinearProgressIndicator
import com.lucdre.idleskills.skills.presentation.util.formatNumber

/**
 * A base composable for displaying a skill's training methods (and tools) panel.
 *
 * @param panelTitle The title text to display for the panel (e.g., "Trees", "Rocks").
 * @param panelBackgroundColor The background color of the main content area of the panel.
 * @param primaryThemeColor The primary color used for text, icons, and progress indication within the panel.
 * @param defaultToolIconRes The default drawable resource ID for the tool icon if the active tool has no specific icon.
 * @param methods List of available training methods for the skill.
 * @param activeMethod Currently selected training method.
 * @param activeTool Currently selected tool for the skill.
 * @param hasBetterToolAvailable Whether a better tool is available for the active skill/tool.
 * @param trainingProgress Progress of the current training action (0-1f).
 * @param onMethodSelected Callback invoked when a training method is selected.
 * @param onToolSelected Callback invoked when the tool icon is clicked.
 * @param methodItemContent A composable lambda responsible for rendering each individual training method item in the grid.
 *                          It receives the [TrainingMethod], a boolean indicating if it's selected,
 *                          and the onMethodSelected callback.
 * @param modifier [Modifier] for this composable.
 */
@Composable
fun SkillTrainingPanel(
    modifier: Modifier = Modifier,
    panelTitle: String,
    panelBackgroundColor: Color,
    primaryThemeColor: Color,
    @DrawableRes defaultToolIconRes: Int,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeTool: Tool?,
    hasBetterToolAvailable: Boolean,
    trainingProgress: Float,
    onMethodSelected: (TrainingMethod) -> Unit,
    onToolSelected: (Tool) -> Unit,
    methodItemContent: @Composable (method: TrainingMethod, isSelected: Boolean, onMethodSelected: (TrainingMethod) -> Unit) -> Unit
) {
    Surface(
        modifier = modifier,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(panelBackgroundColor)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Training method title
                Text(
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    text = panelTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = primaryThemeColor
                )

                // Tool UI
                activeTool?.let { tool ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.clickable { onToolSelected(tool) },
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    // Use a lighter version of panelBackgroundColor or a distinct neutral
                                    .background(panelBackgroundColor.copy(alpha = 0.5f))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = tool.iconResId ?: defaultToolIconRes),
                                    contentDescription = tool.name,
                                    tint = primaryThemeColor
                                )
                            }
                            // Notification bubble when better tool is available
                            if (hasBetterToolAvailable) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                )
                            }
                        }
                        // Tool name
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = primaryThemeColor,
                        )
                    }
                }
                // Training method info UI
                activeMethod?.let { method ->
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = method.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = primaryThemeColor
                            )
                            Text(
                                text = "${method.xpPerAction.formatNumber()} XP | ${method.calculateXpPerHour(activeTool).formatNumber()} XP/h",
                                style = MaterialTheme.typography.bodySmall,
                                color = primaryThemeColor.copy(alpha = 0.7f) // Slightly muted
                            )
                            CustomLinearProgressIndicator(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(8.dp),
                                progress = trainingProgress,
                                progressColor = primaryThemeColor,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
            // Grid of training method icons
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(4), // TODO Potentially make this a parameter if it can vary
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(methods.size) { index ->
                    val currentMethod = methods[index]
                    methodItemContent(
                        currentMethod,
                        currentMethod == activeMethod,
                        onMethodSelected
                    )
                }
            }
        }
    }
}