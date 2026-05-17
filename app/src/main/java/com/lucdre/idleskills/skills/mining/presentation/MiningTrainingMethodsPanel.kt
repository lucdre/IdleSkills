package com.lucdre.idleskills.skills.mining.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lucdre.idleskills.R
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.presentation.SkillMethodIcon
import com.lucdre.idleskills.skills.presentation.SkillTrainingPanel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Mining-specific training methods panel. Uses [SkillTrainingPanel] and [SkillMethodIcon].
 *
 * @param modifier Modifier
 * @param methods List of available Mining training methods
 * @param activeMethod Currently selected training method
 * @param activeCards Currently active cards for mining
 * @param trainingProgress Progress of the current Mining action (0-1)
 * @param onMethodSelected Callback for when a training method is selected by the user
 */
@Composable
fun MiningTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeCards: List<Card> = emptyList(),
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    // Define Mining-specific visual parameters
    val miningPrimaryColor = Color(0xFF37474F)
    val miningSelectedBgColor = Color(0xFFECEFF1)
    val miningIndicatorColor = Color(0xFF455A64)
    val miningUnselectedIconTint = Color(0xFF78909C)
    val miningPanelBackgroundColor = Color(0x1A37474F)

    SkillTrainingPanel(
        modifier = modifier,
        panelTitle = "Rocks",
        panelBackgroundColor = miningPanelBackgroundColor,
        primaryThemeColor = miningPrimaryColor,
        methods = methods,
        activeMethod = activeMethod,
        activeCards = activeCards,
        trainingProgress = trainingProgress,
        onMethodSelected = onMethodSelected,
        methodItemContent = { method, isSelected, onMethodClicked ->
            val imageRes = when (method.name) {
                "Copper Rock" -> R.drawable.ic_tree // TODO: Replace
                "Tin Rock" -> R.drawable.ic_tree       // TODO: Replace
                else -> R.drawable.ic_tree        // TODO: Replace
            }
            SkillMethodIcon(
                method = method,
                isSelected = isSelected,
                onMethodSelected = onMethodClicked,
                imageRes = imageRes,
                selectedBackgroundColor = miningSelectedBgColor,
                selectedIconTint = miningPrimaryColor,
                unselectedIconTint = miningUnselectedIconTint,
                selectionIndicatorColor = miningIndicatorColor
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MiningTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod("Mining", "Copper Rock", 10, 10000),
            TrainingMethod("Mining", "Tin Rock", 15, 10000, 5),
            TrainingMethod("Mining", "Iron Rock", 30, 15000, 20),
            TrainingMethod("Mining", "Coal Rock", 30, 15000, 20),
            TrainingMethod("Mining", "Mithril Rock 2", 30, 15000, 20),
            TrainingMethod("Mining", "Adamant Rock 3", 30, 15000, 20)
        )

        MiningTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[2],
            onMethodSelected = {}
        )
    }
}
