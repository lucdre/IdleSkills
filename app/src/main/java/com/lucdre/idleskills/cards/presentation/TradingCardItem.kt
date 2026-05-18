package com.lucdre.idleskills.cards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.lucdre.idleskills.cards.domain.Card as GameCard
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * A trading card style item for displaying game cards.
 *
 * @param card The card data to display
 * @param onClick Callback when the card is clicked
 */
@Composable
fun TradingCardItem(
    modifier: Modifier = Modifier,
    card: GameCard,
    onClick: () -> Unit
) {
    val skillTheme = SkillMetadata.getTheme(card.type.skillName)
    val cardColor = skillTheme.primaryColor

    Card(
        modifier = modifier
            .width(120.dp)
            .height(180.dp)
            .clickable { onClick() }
            .border(2.dp, cardColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Middle: Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(cardColor.copy(alpha = 0.1f), cardColor.copy(alpha = 0.3f))
                        )
                    )
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = card.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = cardColor
                )

                // Skill badge in corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Gray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(id = card.iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = cardColor
                    )
                }

                // Level textbox
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(4.dp)
                        .background(cardColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LEVEL ${card.level}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Upgrade icon if it meets the criteria
                if (card.quantity>=card.getUpgradeRequirement()){
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(2.dp)
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                    )
                }

            }

        }
    }
}

@Preview
@Composable
fun TradingCardPreview() {
    IdleSkillsTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TradingCardItem(
                card = GameCard("Bronze Axe", CardType.WOODCUTTING_AXE, 1, 12, 0.05f, R.drawable.ic_tree),
                onClick = {}
            )
            TradingCardItem(
                card = GameCard("Steel Pickaxe", CardType.MINING_PICKAXE, 2, 2, 0.12f, R.drawable.ic_tree),
                onClick = {}
            )
        }
    }
}
