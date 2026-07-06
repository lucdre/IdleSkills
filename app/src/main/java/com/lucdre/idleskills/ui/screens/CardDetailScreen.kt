package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.R
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.cards.presentation.CardItemUiState
import com.lucdre.idleskills.cards.presentation.CardRequirementUiState
import com.lucdre.idleskills.cards.presentation.CardUiEffect
import com.lucdre.idleskills.cards.presentation.CardViewModel
import com.lucdre.idleskills.inventory.domain.ItemMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.ui.screens.trainingScreen.components.UpgradeRequirementsSection
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Screen displaying details for a specific card and upgrade requirements.
 *
 * @param cardName The name of the card to display.
 * @param viewModel Destination-scoped ViewModel provided by Hilt.
 * @param onBack Callback to navigate back.
 */
@Composable
fun CardDetailScreen(
    cardName: String,
    viewModel: CardViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardState = uiState.cardsByRarity.values.flatten().find { it.card.name == cardName }

    if (cardState != null) {
        CardDetailScreenContent(
            cardState = cardState,
            uiEffects = viewModel.uiEffects,
            onUpgradeClick = { viewModel.upgradeCard(it) },
            onBack = onBack
        )
    }
}

/**
 * Core content of the Card Detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreenContent(
    cardState: CardItemUiState,
    uiEffects: SharedFlow<CardUiEffect>,
    onUpgradeClick: (Card) -> Unit,
    onBack: () -> Unit
) {
    val card = cardState.card
    val skillTheme = SkillMetadata.getTheme(card.type.skill)
    val skillColor = skillTheme.primaryColor
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        uiEffects.collect { effect ->
            when (effect) {
                is CardUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                CardUiEffect.UpgradeSuccess -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    snackbarHostState.showSnackbar("Card Upgraded!")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(card.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Card Visual
            Box(
                modifier = Modifier
                    .size(200.dp, 300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(skillColor.copy(alpha = 0.1f), skillColor.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = card.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = skillColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Level ${card.level}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ){
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+${(card.efficiencyBonus * 100).toInt()}% ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(R.drawable.bootstrap_arrow_right_circle),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).padding(horizontal = 8.dp),
                            tint = skillColor
                        )
                        Text(
                            text = "+${(cardState.nextLevelBonus * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upgrade Section
            val canUpgrade = cardState.canUpgrade
            val requirements = cardState.requirements

            UpgradeRequirementsSection(requirements = requirements)
                
            Spacer(modifier = Modifier.height(24.dp))
                
            Button(
                onClick = { onUpgradeClick(card) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canUpgrade,
                colors = ButtonDefaults.buttonColors(
                    containerColor = skillColor,
                    disabledContainerColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (canUpgrade) "UPGRADE" else if (requirements.isEmpty()) "MAX LEVEL" else "INSUFFICIENT RESOURCES",
                    color = if (canUpgrade) Color.White else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardDetailScreenPreview() {
    IdleSkillsTheme {
        CardDetailScreenContent(
            cardState = CardItemUiState(
                card = Card(
                    name = "Woodcutting Speed",
                    type = CardType.WOODCUTTING_CARD,
                    level = 1,
                    quantity = 1,
                    efficiencyBonus = 0.05f,
                    iconResId = R.drawable.ic_tree
                ),
                canUpgrade = false,
                nextLevelBonus = 0.10f,
                requirements = listOf(
                    CardRequirementUiState(
                        requiredQuantity = 50,
                        ownedQuantity = 10,
                        metadata = ItemMetadata("Logs", R.drawable.ic_tree)
                    )
                )
            ),
            uiEffects = remember { MutableSharedFlow<CardUiEffect>().asSharedFlow() },
            onUpgradeClick = {},
            onBack = {}
        )
    }
}
