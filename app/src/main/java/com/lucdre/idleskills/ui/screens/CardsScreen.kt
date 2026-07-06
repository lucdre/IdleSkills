package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.presentation.CardItemUiState
import com.lucdre.idleskills.cards.presentation.CardUiEffect
import com.lucdre.idleskills.cards.presentation.CardUiState
import com.lucdre.idleskills.cards.presentation.CardViewModel
import com.lucdre.idleskills.cards.presentation.TradingCardItem
import com.lucdre.idleskills.ui.components.shimmer
import com.lucdre.idleskills.ui.navigation.Route
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.theme.Spacing
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews

/**
 * Screen displaying the collection of cards owned by the player.
 */
@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.uiEffects.collect { effect ->
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
        containerColor = Color.Transparent
    ) { paddingValues ->
        CardsScreenContent(
            modifier = modifier.padding(paddingValues),
            uiState = uiState,
            onCardClick = { card ->
                navController?.navigate(Route.CardDetail(card.name))
            },
            onToggleRarity = viewModel::toggleRarityExpansion
        )
    }
}

@Composable
fun CardsScreenContent(
    modifier: Modifier = Modifier,
    uiState: CardUiState,
    onCardClick: (Card) -> Unit,
    onToggleRarity: (String) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.ScreenEdge)
    ) {
        Text(
            modifier = Modifier.padding(bottom = Spacing.SectionVertical),
            text = "Collection",
            style = MaterialTheme.typography.headlineMedium
        )

        if (uiState.isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.GridItem),
                verticalArrangement = Arrangement.spacedBy(Spacing.GridItem),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(12) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmer()
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.GridItem),
                verticalArrangement = Arrangement.spacedBy(Spacing.GridItem),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
            ) {
                uiState.cardsByRarity.forEach { (rarity, cardStates) ->
                    val isExpanded = uiState.expandedRarities[rarity] ?: true
                    
                    item(key = rarity, span = { GridItemSpan(maxLineSpan) }) {
                        RarityGroupHeader(
                            rarity = rarity,
                            isExpanded = isExpanded,
                            onToggle = { onToggleRarity(rarity) }
                        )
                    }

                    if (isExpanded) {
                        items(cardStates, key = { it.card.type.name }) { cardState ->
                            TradingCardItem(
                                cardState = cardState,
                                onClick = { onCardClick(cardState.card) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RarityGroupHeader(
    rarity: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = rarity,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }
    }
}

@IdleSkillsPreviews
@Composable
fun CardsScreenPreview() {
    IdleSkillsTheme {
        CardsScreenContent(
            uiState = CardUiState(
                cardsByRarity = mapOf(
                    "Common" to listOf(
                        CardItemUiState(
                            card = Card(
                                name = "Woodcutting Speed",
                                type = com.lucdre.idleskills.cards.domain.CardType.WOODCUTTING_CARD,
                                level = 1,
                                quantity = 1,
                                efficiencyBonus = 0.05f,
                                iconResId = com.lucdre.idleskills.R.drawable.ic_tree
                            ),
                            canUpgrade = false,
                            nextLevelBonus = 0.10f
                        ),
                        CardItemUiState(
                            card = Card(
                                name = "Mining Speed",
                                type = com.lucdre.idleskills.cards.domain.CardType.MINING_CARD,
                                level = 1,
                                quantity = 1,
                                efficiencyBonus = 0.05f,
                                iconResId = com.lucdre.idleskills.R.drawable.ic_tree
                            ),
                            canUpgrade = false,
                            nextLevelBonus = 0.10f
                        )
                    )
                )
            ),
            onCardClick = {},
            onToggleRarity = {}
        )
    }
}
