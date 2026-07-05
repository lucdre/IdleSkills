package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.cards.presentation.CardRequirementUiState
import com.lucdre.idleskills.ui.util.NumberFormatter

/**
 * Renders a list of requirements for an upgrade.
 *
 * @param requirements The list of requirements to display.
 */
@Composable
fun UpgradeRequirementsSection(
    requirements: List<CardRequirementUiState>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Upgrade Requirements",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (requirements.isEmpty()) {
            Text(
                text = "Max Level Reached",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            requirements.forEach { req ->
                val isMet = req.ownedQuantity >= req.requiredQuantity
                val textColor = if (isMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = req.metadata.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = req.metadata.displayName,
                            color = Color.LightGray
                        )
                    }
                    Text(
                        text = "${NumberFormatter.formatNumber(req.ownedQuantity)} / ${NumberFormatter.formatNumber(req.requiredQuantity)}",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
