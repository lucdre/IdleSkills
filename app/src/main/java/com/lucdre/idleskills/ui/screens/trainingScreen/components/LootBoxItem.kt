package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.loot.domain.LootBox

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
