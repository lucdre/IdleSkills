package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.inventory.domain.ItemMetadata
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.inventory.presentation.InventoryViewModel
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.ui.components.AutoSizeText
import com.lucdre.idleskills.ui.components.shimmer
import com.lucdre.idleskills.ui.screens.trainingScreen.components.LootBoxItem
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.theme.Spacing
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews
import com.lucdre.idleskills.ui.util.NumberFormatter

@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    InventoryScreenContent(
        modifier = modifier,
        inventoryItems = uiState.inventoryItems,
        lootBoxes = uiState.lootBoxes,
        lastRewards = uiState.lastRewards,
        onOpenBoxClick = { viewModel.onOpenBoxClick(it) },
        clearRewards = { viewModel.clearRewards() },
        isLoading = uiState.isLoading
    )
}

@Composable
fun InventoryScreenContent(
    modifier: Modifier = Modifier,
    inventoryItems: List<Item>,
    lootBoxes: List<LootBox>,
    lastRewards: List<Item>?,
    onOpenBoxClick: (SkillType) -> Unit,
    clearRewards: () -> Unit,
    isLoading: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.ScreenEdge)
        ) {
            Text(
                text = "Inventory",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = Spacing.SectionVertical)
            )

            if (isLoading) {
                InventorySkeleton()
            } else {
                // Loot Boxes Section
                val ownedBoxes = lootBoxes.filter { it.count > 0 }
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
                
                if (inventoryItems.isEmpty()) {
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
                        verticalArrangement = Arrangement.spacedBy(Spacing.GridItem),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.GridItem),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(inventoryItems) { item ->
                            InventoryItemCard(item)
                        }
                    }
                }
            }
        }

        // Rewards Dialog
        lastRewards?.let { rewards ->
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
                        rewards.forEach { item ->
                            Text("${item.metadata.displayName} x${item.quantity}")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun InventorySkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Loot Boxes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Collected Items",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(15) {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmer()
                )
            }
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
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = item.metadata.iconResId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(2.dp))
            AutoSizeText(
                text = item.metadata.displayName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                minFontSize = 8.sp
            )
            AutoSizeText(
                text = NumberFormatter.formatNumber(item.quantity),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                minFontSize = 8.sp
            )
        }
    }
}

@IdleSkillsPreviews
@Composable
fun InventoryScreenPreview() {
    IdleSkillsTheme {
        InventoryScreenContent(
            lootBoxes = listOf(
                LootBox(SkillType.WOODCUTTING, 5)
            ),
            inventoryItems = listOf(
                Item(
                    type = ItemType.OAK_LOGS,
                    quantity = 150,
                    metadata = ItemMetadata("Oak Logs", com.lucdre.idleskills.R.drawable.item_normal_logs)
                )
            ),
            lastRewards = null,
            onOpenBoxClick = {},
            clearRewards = {}
        )
    }
}
