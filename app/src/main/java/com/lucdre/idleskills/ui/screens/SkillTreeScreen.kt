package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeNode
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeNodeType
import com.lucdre.idleskills.prestige.presentation.skilltree.SkillTreeViewModel
import com.lucdre.idleskills.prestige.domain.usecase.GetAvailableSkillTreeNodesUseCase.NodeAvailability
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen for displaying and interacting with the prestige skill tree.
 *
 * @param modifier Modifier for styling
 * @param onClose Callback when the screen should be closed
 * @param viewModel ViewModel handling the skill tree logic
 */
@Composable
fun SkillTreeScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    viewModel: SkillTreeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadSkillTree()
    }

    SkillTreeContent(
        modifier = modifier,
        availablePoints = uiState.availablePoints,
        nodes = uiState.nodes,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onNodeClick = { nodeId -> viewModel.purchaseNode(nodeId) },
        onClose = onClose
    )
}

/**
 * Content for the skill tree screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// todo clean
private fun SkillTreeContent(
    modifier: Modifier = Modifier,
    availablePoints: Int,
    nodes: Map<String, NodeAvailability>,
    isLoading: Boolean,
    errorMessage: String?,
    onNodeClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with close button and points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close"
                )
            }

            Text(
                text = "Skill Tree",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    text = "$availablePoints Points",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Loading state
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Skill tree nodes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "Permanent Upgrades",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(nodes.values.toList()) { nodeAvailability ->
                    SkillTreeNodeItem(
                        nodeAvailability = nodeAvailability,
                        onClick = { onNodeClick(nodeAvailability.node.id) }
                    )
                }
            }
        }
    }
}

/**
 * Individual skill tree node item.
 */
@Composable
private fun SkillTreeNodeItem(
    modifier: Modifier = Modifier,
    nodeAvailability: NodeAvailability,
    onClick: () -> Unit
) {
    val node = nodeAvailability.node
    val alpha = when {
        nodeAvailability.isUnlocked -> 1f
        nodeAvailability.hasPrerequisites -> 0.8f
        else -> 0.4f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .then(
                if (nodeAvailability.canPurchase) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (nodeAvailability.canPurchase) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                nodeAvailability.isUnlocked -> MaterialTheme.colorScheme.secondaryContainer
                nodeAvailability.canPurchase -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (nodeAvailability.canPurchase) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Icon(
                imageVector = when {
                    nodeAvailability.isUnlocked -> Icons.Default.Check
                    nodeAvailability.hasPrerequisites -> Icons.Default.ShoppingCart
                    else -> Icons.Default.Lock
                },
                contentDescription = null,
                tint = when {
                    nodeAvailability.isUnlocked -> MaterialTheme.colorScheme.onSecondaryContainer
                    nodeAvailability.canPurchase -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Node details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = node.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = node.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Prerequisites info
                if (node.prerequisites.isNotEmpty() && !nodeAvailability.isUnlocked) {
                    Text(
                        text = "Requires: ${node.prerequisites.joinString()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Cost
            if (!nodeAvailability.isUnlocked) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (nodeAvailability.canPurchase) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = "${node.cost}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (nodeAvailability.canPurchase) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

/**
 * Helper function to join prerequisite node IDs.
 */
private fun List<String>.joinString(): String {
    return this.joinToString(", ") { nodeId ->
        nodeId.replace("unlock_", "")
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillTreeScreenPreview() {
    IdleSkillsTheme {
        SkillTreeContent(
            availablePoints = 15,
            nodes = mapOf(
                "1" to NodeAvailability(
                    node = SkillTreeNode(
                        id = "1",
                        name = "Efficiency I",
                        description = "Increase action speed by 10%",
                        cost = 5,
                        type = SkillTreeNodeType.SpeedBonus(1.1)
                    ),
                    isUnlocked = true,
                    canPurchase = false,
                    hasPrerequisites = true
                ),
                "2" to NodeAvailability(
                    node = SkillTreeNode(
                        id = "2",
                        name = "Learning I",
                        description = "Increase XP gain by 10%",
                        cost = 10,
                        type = SkillTreeNodeType.ExperienceBonus(1.1)
                    ),
                    isUnlocked = false,
                    canPurchase = true,
                    hasPrerequisites = true
                )
            ),
            isLoading = false,
            errorMessage = null,
            onNodeClick = { },
            onClose = { }
        )
    }
}
