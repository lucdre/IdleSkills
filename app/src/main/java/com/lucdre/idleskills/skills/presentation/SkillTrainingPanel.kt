package com.lucdre.idleskills.skills.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.util.CustomLinearProgressIndicator
import com.lucdre.idleskills.skills.presentation.util.formatNumber

/**
 * A base composable for displaying a skill's training methods panel.
 *
 * @param panelTitle The title text to display for the panel (e.g., "Trees", "Rocks").
 * @param panelBackgroundColor The background color of the main content area of the panel.
 * @param primaryThemeColor The primary color used for text, icons, and progress indication within the panel.
 * @param methods List of available training methods for the skill.
 * @param activeMethod Currently selected training method.
 * @param activeCards Currently active cards for the skill.
 * @param trainingProgress Progress of the current training action (0-1f).
 * @param onMethodSelected Callback invoked when a training method is selected.
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
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeCards: List<Card> = emptyList(),
    trainingProgress: Float,
    onMethodSelected: (TrainingMethod) -> Unit,
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
                                text = "${method.xpPerAction.formatNumber()} XP | ${method.calculateXpPerHour(activeCards).formatNumber()} XP/h",
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
            val columns = 4
            val rows = methods.chunked(columns)
            
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rows.forEach { rowMethods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowMethods.forEach { currentMethod ->
                            Box(modifier = Modifier.weight(1f)) {
                                methodItemContent(
                                    currentMethod,
                                    currentMethod == activeMethod,
                                    onMethodSelected
                                )
                            }
                        }
                        // Fill empty cells if the last row is not full
                        if (rowMethods.size < columns) {
                            repeat(columns - rowMethods.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
