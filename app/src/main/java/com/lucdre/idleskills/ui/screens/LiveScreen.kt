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
import com.lucdre.idleskills.prestige.presentation.PrestigeUiState
import com.lucdre.idleskills.prestige.presentation.PrestigeViewModel
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.ExpandableSkillItem
import com.lucdre.idleskills.skills.presentation.SkillListUiState
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.components.OfflineProgressPopup
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen where players can see their character training and catch random loot boxes.
 * Also integrated with Skill management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveScreenViewModel = hiltViewModel(),
    skillViewModel: SkillListViewModel = hiltViewModel(),
    prestigeViewModel: PrestigeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val skillUiState by skillViewModel.uiState.collectAsStateWithLifecycle()
    val prestigeUiState by prestigeViewModel.uiState.collectAsStateWithLifecycle()
    val trainingProgress by skillViewModel.trainingProgress.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    var showSkillTree by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.setScreenVisible(true)
        onDispose {
            viewModel.setScreenVisible(false)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LiveScreenContent(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            skillUiState = skillUiState,
            prestigeUiState = prestigeUiState,
            trainingProgress = trainingProgress,
            onSpriteClick = { viewModel.onSpriteClick() },
            onClearRewards = { viewModel.clearRewards() },
            onToggleInventory = { visible -> viewModel.toggleInventory(visible) },
            onToggleExpand = { skillName ->
                skillViewModel.toggleSkillExpansion(skillName)
            },
            onMethodSelected = { skillViewModel.selectTrainingMethod(it) },
            onPrestigeClick = {
                prestigeViewModel.prestige(
                    resetTrainingState = {
                        skillViewModel.resetTrainingState()
                    }
                )
            },
            onSkillTreeClick = {
                showSkillTree = true
            }
        )

        // Offline Progress Popup
        skillUiState.offlineProgress?.let { result ->
            OfflineProgressPopup(
                result = result,
                onDismiss = { skillViewModel.dismissOfflineProgress() }
            )
        }

        // Skill Tree Screen as overlay
        if (showSkillTree) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                SkillTreeScreen(
                    onClose = { showSkillTree = false }
                )
            }
        }

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
}

/**
 * Stateless content for the Live screen.
 */
@Composable
fun LiveScreenContent(
    modifier: Modifier = Modifier,
    uiState: LiveScreenUiState,
    skillUiState: SkillListUiState,
    prestigeUiState: PrestigeUiState,
    trainingProgress: Float,
    onSpriteClick: () -> Unit,
    onClearRewards: () -> Unit,
    onToggleInventory: (Boolean) -> Unit,
    onToggleExpand: (String) -> Unit,
    onMethodSelected: (TrainingMethod) -> Unit,
    onPrestigeClick: () -> Unit,
    onSkillTreeClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(getBiomeGradient(uiState.activeTrainingSkill?.displayName))
    ) {
        // --- 1. GAME SCENE (Character & Object) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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

            // --- 2. DROPS (Now inside the Game Scene Box) ---
            if (uiState.isSpriteVisible) {
                val spriteIcon = getIconForSkill(uiState.activeTrainingSkill?.displayName)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp)
                ) {
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

            // --- 3. HUD (Now inside the Game Scene Box) ---
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
        }

        // --- 4. SKILL LIST (Bottom Half) ---
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f), // Slightly transparent to see background
            shape = MaterialTheme.shapes.large // Maybe some rounded corners at the top?
        ) {
            SkillListScreenContents(
                modifier = Modifier.fillMaxSize(),
                skillUiState = skillUiState,
                prestigeUiState = prestigeUiState,
                trainingProgress = trainingProgress,
                onSkillClick = { },
                onToggleExpand = onToggleExpand,
                onMethodSelected = onMethodSelected,
                onPrestigeClick = onPrestigeClick,
                onSkillTreeClick = onSkillTreeClick
            )
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

/**
 * Renders the skill list content.
 */
@Composable
fun SkillListScreenContents(
    modifier: Modifier = Modifier,
    skillUiState: SkillListUiState,
    prestigeUiState: PrestigeUiState,
    trainingProgress: Float,
    onSkillClick: (Skill) -> Unit,
    onToggleExpand: (String) -> Unit,
    onMethodSelected: (TrainingMethod) -> Unit,
    onPrestigeClick: () -> Unit,
    onSkillTreeClick: () -> Unit
) {
    val expandedSkillName = skillUiState.expandedSkillName
    Column(modifier = modifier) {
        if (skillUiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (skillUiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${skillUiState.error}")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(skillUiState.skills, key = { it.name }) { skill ->
                    val isActiveSkill = skill.name == skillUiState.activeSkill
                    val isSelectedSkill = skill.name == expandedSkillName

                    ExpandableSkillItem(
                        skill = skill,
                        isActive = isActiveSkill,
                        isExpanded = isSelectedSkill,
                        xpPerHour = if (isActiveSkill) {
                            skillUiState.activeTrainingMethod?.calculateXpPerHour(skillUiState.activeCards)
                                ?: 3600
                        } else {
                            0
                        },
                        trainingMethods = if (isSelectedSkill) skillUiState.trainingMethods else emptyList(),
                        activeMethod = if (isActiveSkill) skillUiState.activeTrainingMethod else null,
                        activeCards = if (isActiveSkill) skillUiState.activeCards else emptyList(),
                        trainingProgress = if (isActiveSkill) trainingProgress else 0f,
                        onSkillClick = onSkillClick,
                        onToggleExpand = { onToggleExpand(skill.name) },
                        onMethodSelected = onMethodSelected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
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
    onOpenBoxClick: (SkillType) -> Unit
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
                    LootBoxItem(box = box, onOpenClick = { onOpenBoxClick(box.skill) })
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
            skillUiState = SkillListUiState(
                skills = listOf(
                    Skill("Woodcutting", 10, 1500),
                    Skill("Fishing", 20, 4200)
                ),
                isLoading = false
            ),
            prestigeUiState = PrestigeUiState(),
            trainingProgress = 0.5f,
            onSpriteClick = {},
            onClearRewards = {},
            onToggleInventory = {},
            onToggleExpand = {},
            onMethodSelected = {},
            onPrestigeClick = {},
            onSkillTreeClick = {}
        )
    }
}
