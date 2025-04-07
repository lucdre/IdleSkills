package com.lucdre.idleskills.skills.woodcutting.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.TrainingMethod
import com.lucdre.idleskills.skills.presentation.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

@Composable
fun WcTrainingMethodsPanel(
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0x0A4CAF50)) // Very light green background
        ) {
            Text(
                text = "Woodcutting Methods",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2E7D32), // Darker green for woodcutting theme
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 8.dp)
            ) {
                items(methods) { method ->
                    WcMethodItem(
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


@Composable
fun WcMethodItem(
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
                Color(0xFFE8F5E9) // Light green background for selected
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
                    Color(0xFF2E7D32) // Darker green for selected text
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

                // XP Progress bar
                CustomLinearProgressIndicator(
                    progress = trainingProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    progressColor = if (isSelected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chopping",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50) // Green for active
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WcTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod("Woodcutting", "Tree", 10, 10000),
            TrainingMethod("Woodcutting", "Oak", 15, 10000, 5),
            TrainingMethod("Woodcutting", "Willow", 30, 15000, 20)
        )

        WcTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[1],
            onMethodSelected = {}
        )
    }
}