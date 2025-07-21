package com.lucdre.idleskills.skills.mining.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.util.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Mining-specific training methods panel.
 *
 * @param modifier Modifier
 * @param methods List of available Mining training methods
 * @param activeMethod Currently selected training method
 * @param activeTool Currently selected Mining tool
 * @param hasBetterToolAvailable Whether a better tool is available
 * @param trainingProgress Progress of the current Mining action (0-1)
 * @param onMethodSelected Callback for when a training method is selected by the user
 * @param onToolSelected Callback for when a tool is selected by the user
 */
@Composable
fun MiningTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeTool: Tool?,
    hasBetterToolAvailable: Boolean = false,
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit,
    onToolSelected: (Tool) -> Unit
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
                .background(Color(0xFFD3D3D3)) // Very light gray background
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Training method title
                Text(
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    text = "Rocks",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xff5a5a5a) // Darker gray for mining theme
                )

                // Tool UI
                activeTool?.let { tool ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tool icon with notification and click functionality. TopEnd Alignment for the icon
                        Box(
                            modifier = Modifier.clickable { onToolSelected(tool) },
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD3D3D3)) // Very light gray background
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = tool.iconResId ?: R.drawable.ic_tree),
                                    contentDescription = tool.name,
                                    tint = Color(0xff5a5a5a)
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
                            color = Color(0xff5a5a5a),
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
                                color = Color(0xff5a5a5a)
                            )

                            Text(
                                text = "${method.xpPerAction} XP | ${method.calculateXpPerHour(activeTool)} XP/h",
                                style = MaterialTheme.typography.bodySmall
                            )

                            CustomLinearProgressIndicator(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(8.dp),
                                progress = trainingProgress,
                                progressColor = Color(0xff5a5a5a),
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // Grid of training method icons
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(methods.size) { index ->
                    val method = methods[index]
                    MiningMethodIcon(
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
 * Icon representing a mining training method.
 *
 *
 *
 * @param method The mining training method to display
 * @param isSelected Whether this method is currently selected/active
 * @param onMethodSelected Callback for when this method is selected by the user
 */
@Composable
fun MiningMethodIcon(
    method: TrainingMethod,
    isSelected: Boolean,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    // Placeholder icons //TODO
    val imageRes = when (method.name) {
        "Copper Rock" -> R.drawable.ic_tree
        "Tin Rock" -> R.drawable.ic_tree
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
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = imageRes),
                contentDescription = method.name,
                tint = if (isSelected) Color(0xff5a5a5a) else Color.LightGray
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
fun MiningTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod("Mining", "Copper Rock", 10, 10000),
            TrainingMethod("Mining", "Tin Rock", 15, 10000, 5),
            TrainingMethod("Mining", "Iron Rock", 30, 15000, 20),
            TrainingMethod("Mining", "Coal Rock", 30, 15000, 20),
            TrainingMethod("Mining", "Mithril Rock 2", 30, 15000, 20),
            TrainingMethod("Mining", "Adamant Rock 3", 30, 15000, 20)
        )

        val tool = Tool("Mining", "Iron Pickaxe", 1.2f, 5, R.drawable.ic_tree)

        MiningTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[3],
            activeTool = tool,
            hasBetterToolAvailable = true,
            onMethodSelected = {},
            onToolSelected = {}
        )
    }
}
