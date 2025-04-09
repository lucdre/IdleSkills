package com.lucdre.idleskills.skills.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Default panel for displaying training methods and related information for a skill.
 *
 * @param modifier Modifier
 * @param methods List of training methods to display
 * @param activeMethod Currently selected training method
 * @param trainingProgress Progress of the current training action (0-1)
 * @param onMethodSelected Callback for when a method is selected by the user
 */
@Composable
fun DefaultTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Training Methods",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(methods) { method ->
                    CompactTrainingMethodItem(
                        method = method,
                        isSelected = method == activeMethod,
                        trainingProgress = if (method == activeMethod) trainingProgress else 0f,
                        onMethodSelected = onMethodSelected
                    )
                }
            }
        }
    }
}

/**
 * A compact card displaying a single training method.
 *
 * @param method The training method to display
 * @param isSelected Whether this method is currently selected/active
 * @param trainingProgress Progress of the current training action (0-1)
 * @param onMethodSelected Callback for when this method is selected by the user
 */
@Composable
fun CompactTrainingMethodItem(
    method: TrainingMethod,
    isSelected: Boolean,
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .wrapContentHeight()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMethodSelected(method) }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = method.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${method.xpPerAction} XP",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${method.calculateXpPerHour()} XP/h",
                style = MaterialTheme.typography.bodySmall
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    progress = { trainingProgress },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod("Woodcutting", "Tree", 10, 10000),
            TrainingMethod("Woodcutting", "Oak", 15, 10000, 5),
            TrainingMethod("Woodcutting", "Willow", 30, 15000, 20)
        )

        DefaultTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[1],
            onMethodSelected = {}
        )
    }
}