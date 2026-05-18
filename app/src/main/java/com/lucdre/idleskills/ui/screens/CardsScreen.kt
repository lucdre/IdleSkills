package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.presentation.CardUiState
import com.lucdre.idleskills.cards.presentation.CardViewModel
import com.lucdre.idleskills.cards.presentation.TradingCardItem
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen displaying the collection of cards owned by the player.
 */
@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedCard by remember { mutableStateOf<Card?>(null) }

    if (selectedCard != null) {
        val cards = uiState.cardsBySkill.values.flatten()
        val currentCard = cards.find { it.name == selectedCard!!.name && it.type == selectedCard!!.type } ?: selectedCard!!
        
        CardDetailScreen(
            card = currentCard,
            onBack = { selectedCard = null },
            onUpgrade = { viewModel.upgradeCard(it) }
        )
    } else {
        CardsScreenContent(
            modifier = modifier,
            uiState = uiState,
            onCardClick = { selectedCard = it }
        )
    }
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
            ) {
                uiState.cardsBySkill.forEach { (skillName, cards) ->
                    val isExpanded = expandedSkills[skillName] ?: true
                    
                    item(key = skillName) {
                        SkillGroupHeader(
                            skillName = skillName,
                            isExpanded = isExpanded,
                            onToggle = { expandedSkills[skillName] = !isExpanded }
                        )
                    }

                    if (isExpanded) {
                        // Display cards in rows of 2 within the LazyColumn
                        val columns = 3
                        val rows = cards.chunked(columns)
                        items(rows) { rowCards ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowCards.forEach { card ->
                                    TradingCardItem(
                                        modifier = Modifier.weight(1f),
                                        card = card,
                                        onClick = { onCardClick(card) }
                                    )
                                }

                                // Fill empty cells if the last row is not full
                                if (rowCards.size < columns) {
                                    repeat(columns - rowCards.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
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

@Preview(showBackground = true)
@Composable
fun CardsScreenPreview() {
    IdleSkillsTheme {
        CardsScreenContent(
            uiState = CardUiState(
                cardsBySkill = mapOf(
                    "Woodcutting" to listOf(
                        Card("Bronze Axe", com.lucdre.idleskills.cards.domain.CardType.WOODCUTTING_AXE, 2, 5, 0.05f)
                    ),
                    "Mining" to listOf(
                        Card("Bronze Pickaxe", com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE, 1, 2, 0.05f) ,
                        Card("Bronze Pickaxe", com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE, 1, 2, 0.05f)
                    ),
                    "Fishing" to listOf(
                        Card("Bronze Pickaxe", com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE, 1, 2, 0.05f) ,
                        Card("Bronze Pickaxe", com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE, 1, 2, 0.05f),
                        Card("Bronze Pickaxe", com.lucdre.idleskills.cards.domain.CardType.MINING_PICKAXE, 1, 2, 0.05f)
                    )
                )
            ),
            onCardClick = {}
        )
    }
}
