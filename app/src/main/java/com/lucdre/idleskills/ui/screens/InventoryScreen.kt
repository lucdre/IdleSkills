package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.main.presentation.TrainingViewModel
import com.lucdre.idleskills.ui.screens.trainingScreen.components.LootBoxItem
import com.lucdre.idleskills.ui.util.NumberFormatter

@Composable
fun InventoryScreen(
    viewModel: TrainingViewModel,
    modifier: Modifier = Modifier
) {
    val lootState by viewModel.lootState.collectAsStateWithLifecycle()
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()
    
    InventoryScreenContent(
        lootState = lootState,
        sessionState = sessionState,
        onOpenBoxClick = { viewModel.onOpenBoxClick(it) },
        clearRewards = { viewModel.clearRewards() },
        modifier = modifier
    )
}

@Composable
fun InventoryScreenContent(
    lootState: com.lucdre.idleskills.main.presentation.TrainingLootState,
    sessionState: com.lucdre.idleskills.main.presentation.TrainingSessionState,
    onOpenBoxClick: (com.lucdre.idleskills.skills.domain.skill.SkillType) -> Unit,
    clearRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Inventory",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Loot Boxes Section
            val ownedBoxes = lootState.lootBoxes.filter { it.count > 0 }
            if (ownedBoxes.isNotEmpty()) {
                Text(
                    text = "Loot Boxes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(ownedBoxes) { box ->
                        LootBoxItem(box = box, onOpenClick = { onOpenBoxClick(box.skill) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Collected Items Section
            Text(
                text = "Collected Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (sessionState.inventoryItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items gathered yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(sessionState.inventoryItems) { item ->
                        InventoryItemCard(item)
                    }
                }
            }
        }

        // Rewards Dialog
        lootState.lastRewards?.let { rewards ->
            AlertDialog(
                onDismissRequest = clearRewards,
                confirmButton = {
                    TextButton(onClick = clearRewards) {
                        Text("Awesome!")
                    }
                },
                title = { Text("You found items!") },
                text = {
                    Column {
                        rewards.forEach { (type, quantity) ->
                            val displayName = type.name.split("_")
                                .joinToString(" ") { word ->
                                    word.lowercase().replaceFirstChar { it.uppercase() }
                                }
                            Text("$displayName x$quantity")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun InventoryItemCard(item: Item) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = item.type.iconResId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.type.displayName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = NumberFormatter.formatNumber(item.quantity),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@IdleSkillsPreviews
@Composable
fun InventoryScreenPreview() {
    IdleSkillsTheme {
        InventoryScreenContent(
            lootState = com.lucdre.idleskills.main.presentation.TrainingLootState(
                lootBoxes = listOf(
                    com.lucdre.idleskills.loot.domain.LootBox(com.lucdre.idleskills.skills.domain.skill.SkillType.WOODCUTTING, 5)
                )
            ),
            sessionState = com.lucdre.idleskills.main.presentation.TrainingSessionState(
                inventoryItems = listOf(
                    com.lucdre.idleskills.inventory.domain.Item(com.lucdre.idleskills.inventory.domain.ItemType.OAK_LOGS, 150)
                )
            ),
            onOpenBoxClick = {},
            clearRewards = {}
        )
    }
}
