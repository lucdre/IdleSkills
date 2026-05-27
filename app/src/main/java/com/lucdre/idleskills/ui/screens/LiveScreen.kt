package com.lucdre.idleskills.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.main.presentation.LiveScreenUiState
import com.lucdre.idleskills.main.presentation.LiveScreenViewModel
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen where players can see their character training and catch random loot boxes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    DisposableEffect(Unit) {
        viewModel.setScreenVisible(true)
        onDispose {
            viewModel.setScreenVisible(false)
        }
    }

    LiveScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSpriteClick = { viewModel.onSpriteClick() },
        onClearRewards = { viewModel.clearRewards() },
        onToggleInventory = { visible -> viewModel.toggleInventory(visible) }
    )

    if (uiState.isInventoryVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleInventory(false) },
            sheetState = sheetState
        ) {
            InventorySheetContent(
                lootBoxes = uiState.lootBoxes,
                onOpenBoxClick = { viewModel.onOpenBoxClick(it) }
            )
        }
    }
}

/**
 * Stateless content for the Live screen.
 */
@Composable
fun LiveScreenContent(
    modifier: Modifier = Modifier,
    uiState: LiveScreenUiState,
    onSpriteClick: () -> Unit,
    onClearRewards: () -> Unit,
    onToggleInventory: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(getBiomeGradient(uiState.activeTrainingSkill?.displayName))
    ) {
        // --- 1. GAME SCENE (Character & Object) ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.activeTrainingSkill != null) {
                // The training target (Tree/Rock/etc)
                TrainingTarget(skillName = uiState.activeTrainingSkill.displayName)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // The character performing the action
                AnimatedCharacter(skillName = uiState.activeTrainingSkill.displayName)
            } else {
                Text(
                    text = "Start training a skill!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // --- 2. DROPS ---
        if (uiState.isSpriteVisible) {
            val spriteIcon = getIconForSkill(uiState.activeTrainingSkill?.displayName)
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp)
            ) {
                // Pre-calculate bias to avoid constant recomposition during state changes
                val alignment = remember(uiState.spritePosition) {
                    BiasAlignment(
                        horizontalBias = (uiState.spritePosition.x * 2) - 1,
                        verticalBias = (uiState.spritePosition.y * 2) - 1
                    )
                }
                
                Box(
                    modifier = Modifier
                        .align(alignment)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                        .clickable { onSpriteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = spriteIcon,
                        contentDescription = "Loot!",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // --- 3. HUD ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.activeTrainingSkill != null) {
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = CircleShape,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val skillName = uiState.activeTrainingSkill.displayName
                    val displayText = if (uiState.activeTrainingMethod != null) {
                        "$skillName: ${uiState.activeTrainingMethod}"
                    } else {
                        skillName
                    }
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Inventory Button
        FloatingActionButton(
            onClick = { onToggleInventory(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            BadgedBox(
                badge = {
                    val totalBoxes = uiState.lootBoxes.sumOf { it.count }
                    if (totalBoxes > 0) {
                        Badge { Text(totalBoxes.toString()) }
                    }
                }
            ) {
                Icon(Icons.Default.Inventory, contentDescription = "Inventory")
            }
        }

        // Rewards Dialog
        uiState.lastRewards?.let { rewards ->
            AlertDialog(
                onDismissRequest = onClearRewards,
                confirmButton = {
                    TextButton(onClick = onClearRewards) {
                        Text("Awesome!")
                    }
                },
                title = { Text("You found items!") },
                text = {
                    Column {
                        rewards.forEach { (type, quantity) ->
                            Text("${getCardDisplayName(type)} x$quantity")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TrainingTarget(skillName: String) { //TODO specific icons for specific training methods.
    val icon = when (skillName) {
        "Woodcutting" -> Icons.Default.Forest
        "Mining" -> Icons.Default.Terrain
        "Fishing" -> Icons.Default.Waves
        else -> Icons.Default.QuestionMark
    }
    
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(120.dp),
        tint = Color.White.copy(alpha = 0.8f)
    )
}

@Composable
fun AnimatedCharacter(skillName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "CharacterAnimation")
    
    val characterAnimationModifier = when (skillName) {
        "Woodcutting" -> {
            val rotation by infiniteTransition.animateFloat(
                initialValue = -20f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "AxeSwing"
            )
            Modifier.graphicsLayer { rotationZ = rotation }
        }
        "Mining" -> {
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PickaxeHit"
            )
            Modifier.offset { IntOffset(0, offset.dp.roundToPx()) }
        }
        "Fishing" -> {
            val bobbing by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "FishingBob"
            )
            Modifier.offset { IntOffset(0, bobbing.dp.roundToPx()) }
        }
        else -> Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = characterAnimationModifier
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Character",
            modifier = Modifier.size(80.dp),
            tint = Color.White
        )
        
        // Tool icon
        val toolIcon = when (skillName) {
            "Woodcutting" -> Icons.Default.Handyman // Placeholder for Axe
            "Mining" -> Icons.Default.Hardware // Placeholder for Pickaxe
            "Fishing" -> Icons.Default.Phishing // Placeholder for Rod
            else -> null
        }
        
        toolIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(32.dp).offset(x = 20.dp, y = (-40).dp),
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun InventorySheetContent(
    lootBoxes: List<LootBox>,
    onOpenBoxClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp) // Extra padding for system bars
    ) {
        Text(
            text = "Your Loot Boxes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val ownedBoxes = lootBoxes.filter { it.count > 0 }
        
        if (ownedBoxes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Empty.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ownedBoxes) { box ->
                    LootBoxItem(box = box, onOpenClick = { onOpenBoxClick(box.skill.displayName) })
                }
            }
        }
    }
}

@Composable
fun LootBoxItem(box: LootBox, onOpenClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = box.getDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Owned: ${box.count}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Button(onClick = onOpenClick) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open")
            }
        }
    }
}

private fun getBiomeGradient(skillName: String?): Brush {
    val colors = if (skillName != null) {
        SkillMetadata.getTheme(skillName).biomeColors
    } else {
        listOf(Color(0xFF121212), Color(0xFF000000))
    }
    return Brush.verticalGradient(colors)
}

private fun getIconForSkill(skillName: String?): ImageVector {
    return when (skillName) {
        "Woodcutting" -> Icons.Default.Forest
        "Mining" -> Icons.Default.Terrain
        "Fishing" -> Icons.Default.SetMeal
        else -> Icons.Default.Star
    }
}

private fun getCardDisplayName(type: CardType): String {
    return when (type) {
        CardType.WOODCUTTING_AXE -> "Bronze Axe"
        CardType.MINING_PICKAXE -> "Bronze Pickaxe"
        CardType.FISHING_NET -> "Small Fishing Net"
        CardType.FISHING_ROD -> "Fishing Rod"
        CardType.FISHING_HARPOON -> "Harpoon"
        CardType.FISHING_LOBSTER_CAGE -> "Lobster Cage"
    }
}

@Preview(showBackground = true)
@Composable
fun LiveScreenPreview() {
    IdleSkillsTheme {
        LiveScreenContent(
            uiState = LiveScreenUiState(
                lootBoxes = listOf(
                    LootBox(SkillType.WOODCUTTING, 5),
                    LootBox(SkillType.MINING, 2)
                ),
                isSpriteVisible = true,
                spritePosition = Offset(0.55f, 0.45f),
                activeTrainingSkill = SkillType.WOODCUTTING
            ),
            onSpriteClick = {},
            onClearRewards = {},
            onToggleInventory = {}
        )
    }
}
