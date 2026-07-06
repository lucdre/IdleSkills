package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.LocalItemRegistry

@Composable
fun TrainingMethodSelector(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    selectedMethodType: TrainingMethodType?,
    onMethodSelected: (TrainingMethodType) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Training Method",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            methods.forEach { method ->
                TrainingMethodCard(
                    method = method,
                    selected = method.type == selectedMethodType,
                    onClick = { onMethodSelected(method.type) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TrainingMethodSelectorPreview() {
    IdleSkillsTheme {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalItemRegistry provides ItemRegistry()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                TrainingMethodSelector(
                    methods = listOf(
                        TrainingMethod(
                            type = TrainingMethodType.WC_TREE,
                            xpPerAction = 25,
                            actionDurationMs = 5000,
                            producedItemType = ItemType.NORMAL_LOGS
                        ),
                        TrainingMethod(
                            type = TrainingMethodType.WC_OAK,
                            xpPerAction = 37,
                            actionDurationMs = 4000,
                            producedItemType = ItemType.OAK_LOGS
                        )
                    ),
                    selectedMethodType = TrainingMethodType.WC_OAK,
                    onMethodSelected = {}
                )
            }
        }
    }
}
