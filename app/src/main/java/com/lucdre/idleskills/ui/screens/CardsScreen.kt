package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.lucdre.idleskills.ui.components.shimmer
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.presentation.CardUiState
import com.lucdre.idleskills.cards.presentation.CardViewModel
import com.lucdre.idleskills.cards.presentation.TradingCardItem
import com.lucdre.idleskills.ui.navigation.Routes
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

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

    CardsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onCardClick = { card ->
            navController?.navigate("${Routes.CARD_DETAIL}/${card.name}")
        }
    )
}

@Composable
fun CardsScreenContent(
    modifier: Modifier = Modifier,
    uiState: CardUiState,
    onCardClick: (Card) -> Unit
) {
    val expandedSkills = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Collection",
            style = MaterialTheme.typography.headlineMedium
        )

        if (uiState.isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
            ) {
                uiState.cardsBySkill.forEach { (skillName, cards) ->
                    val isExpanded = expandedSkills[skillName] ?: true
                    
                    item(key = skillName, span = { GridItemSpan(maxLineSpan) }) {
                        SkillGroupHeader(
                            skillName = skillName,
                            isExpanded = isExpanded,
                            onToggle = { expandedSkills[skillName] = !isExpanded }
                        )
                    }

                    if (isExpanded) {
                        items(cards, key = { it.name }) { card ->
                            TradingCardItem(
                                card = card,
                                onClick = { onCardClick(card) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillGroupHeader(
    skillName: String,
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
                text = skillName,
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
                cardsBySkill = mapOf(
                    "Woodcutting" to listOf(
                        Card(
                            name = "Bronze Axe",
                            type = com.lucdre.idleskills.cards.domain.CardType.WOODCUTTING_AXE,
                            level = 1,
                            quantity = 5,
                            efficiencyBonus = 0.05f,
                            iconResId = com.lucdre.idleskills.R.drawable.ic_tree
                        )
                    ),
                    "Mining" to listOf(
                        Card(
                            name = "Bronze Pickaxe",
                            type = com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE,
                            level = 1,
                            quantity = 2,
                            efficiencyBonus = 0.05f,
                            iconResId = com.lucdre.idleskills.R.drawable.ic_tree
                        ),
                        Card(
                            name = "Steel Pickaxe",
                            type = com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE,
                            level = 2,
                            quantity = 1,
                            efficiencyBonus = 0.10f,
                            iconResId = com.lucdre.idleskills.R.drawable.ic_tree
                        )
                    ),
                    "Fishing" to listOf(
                        Card(
                            name = "Small Net",
                            type = com.lucdre.idleskills.cards.domain.CardType.FISHING_NET,
                            level = 1,
                            quantity = 10,
                            efficiencyBonus = 0.05f,
                            iconResId = com.lucdre.idleskills.R.drawable.ic_tree
                        )
                    )
                )
            ),
            onCardClick = {}
        )
    }
}
