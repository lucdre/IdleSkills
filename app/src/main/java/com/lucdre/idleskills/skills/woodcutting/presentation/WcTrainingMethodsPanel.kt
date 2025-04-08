package com.lucdre.idleskills.skills.woodcutting.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.tools.Tool

@Composable
fun WcTrainingMethodsPanel(
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeTool: Tool?,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Training method title
                Text(
                    text = "Trees",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32), // Darker green for woodcutting theme
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                )

                // Tool UI
                activeTool?.let { tool ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tool icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x33A5D6A7)) // Very light green background
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = tool.iconResId ?: R.drawable.ic_tree),
                                contentDescription = tool.name,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Tool name
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32),)
                    }
                }


                // Training method info UI
                activeMethod?.let { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = method.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF2E7D32)
                            )

                            Text(
                                text = "${method.xpPerAction} XP | ${method.calculateXpPerHour()} XP/h",
                                style = MaterialTheme.typography.bodySmall
                            )

                            CustomLinearProgressIndicator(
                                progress = trainingProgress,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(8.dp),
                                progressColor = Color(0xFF4CAF50),
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // Grid of training method icons
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(methods.size) { index ->
                    val method = methods[index]
                    WcMethodIcon(
                        method = method,
                        isSelected = method == activeMethod,
                        onMethodSelected = onMethodSelected
                    )
                }
            }
        }
    }
}

/**
 * The training method icons.
 *
 */
@Composable
fun WcMethodIcon(
    method: TrainingMethod,
    isSelected: Boolean,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    val imageRes = when(method.name) {
        "Tree" -> R.drawable.ic_tree
        "Oak Tree" -> R.drawable.ic_tree
        "Willow Tree" -> R.drawable.ic_tree
        "Cheat Tree" -> R.drawable.ic_tree
        else -> R.drawable.ic_tree // Default
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onMethodSelected(method) }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = imageRes),
                contentDescription = method.name,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
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
            TrainingMethod("Woodcutting", "Willow Tree Tree Thisisatee", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Willow", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Willow", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Willow", 30, 15000, 20)
        )

        val tool = Tool("Woodcutting", "Iron Axe", 1.2f, 5, R.drawable.ic_tree)

        WcTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[1],
            activeTool = tool,
            onMethodSelected = {}
        )
    }
}