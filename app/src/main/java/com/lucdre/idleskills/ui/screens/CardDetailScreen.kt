package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucdre.idleskills.R
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.cards.presentation.CardItemUiState
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.components.CustomLinearProgressIndicator

/**
 * Screen displaying details for a specific card and upgrade requirements.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    cardState: CardItemUiState,
    onBack: () -> Unit,
    onUpgrade: (Card) -> Unit = {}
) {
    val card = cardState.card
    val skillTheme = SkillMetadata.getTheme(card.type.skill)
    val skillColor = skillTheme.primaryColor

    Scaffold(
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
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            painter = painterResource(id = card.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = skillColor
                        )
                        Text(
                            text = "+${(card.efficiencyBonus * 100).toInt()}% ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(R.drawable.bootstrap_arrow_right_circle),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = skillColor
                        )
                        Icon(
                            painter = painterResource(id = card.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Upgrade Section
            val requirement = cardState.upgradeRequirement
            val progress = (card.quantity.toFloat() / requirement.toFloat()).coerceIn(0f, 1f)
            val canUpgrade = cardState.canUpgrade

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cards Owned",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                    Text(
                        text = "${card.quantity} / $requirement",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (canUpgrade) Color(0xFF4CAF50) else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    progress = progress,
                    progressColor = if (canUpgrade) Color(0xFF4CAF50) else skillColor,
                    backgroundColor = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { onUpgrade(card) },
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
                        text = if (canUpgrade) "UPGRADE" else "NEED MORE CARDS",
                        color = if (canUpgrade) Color.White else Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardDetailScreenPreview() {
    IdleSkillsTheme {
        CardDetailScreen(
            cardState = CardItemUiState(
                card = Card(
                    name = "Bronze Axe",
                    type = CardType.WOODCUTTING_AXE,
                    level = 1,
                    quantity = 5,
                    efficiencyBonus = 0.05f,
                    iconResId = R.drawable.ic_tree
                ),
                upgradeRequirement = 10,
                canUpgrade = false,
                nextLevelBonus = 0.10f
            ),
            onBack = {}
        )
    }
}
