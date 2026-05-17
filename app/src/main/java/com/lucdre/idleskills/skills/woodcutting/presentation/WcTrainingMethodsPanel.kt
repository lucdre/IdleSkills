package com.lucdre.idleskills.skills.woodcutting.presentation

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
 * Woodcutting-specific training methods panel. Uses [SkillTrainingPanel] and [SkillMethodIcon].
 *
 * @param modifier Modifier
 * @param methods List of available woodcutting training methods
 * @param activeMethod Currently selected training method
 * @param activeCards Currently active cards for woodcutting
 * @param trainingProgress Progress of the current woodcutting action (0-1)
 * @param onMethodSelected Callback for when a training method is selected by the user
 */
@Composable
fun WcTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeCards: List<Card> = emptyList(),
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    // Define Woodcutting-specific visual parameters
    val wcPrimaryColor = Color(0xFF2E7D32)
    val wcSelectedBgColor = Color(0xFFE8F5E9)
    val wcIndicatorColor =  Color(0xFF4CAF50)
    val wcUnselectedIconTint = Color.Gray
    val wcPanelBackgroundColor = Color(0x0A4CAF50)


    SkillTrainingPanel(
        modifier = modifier,
        panelTitle = "Trees",
        panelBackgroundColor = wcPanelBackgroundColor,
        primaryThemeColor = wcPrimaryColor,
        methods = methods,
        activeMethod = activeMethod,
        activeCards = activeCards,
        trainingProgress = trainingProgress,
        onMethodSelected = onMethodSelected,
        methodItemContent = { method, isSelected, onMethodClicked ->
            val imageRes = when (method.name) {
                "Tree" -> R.drawable.ic_tree // TODO: Replace
                "Oak Tree" -> R.drawable.ic_tree // TODO: Replace
                "Willow Tree" -> R.drawable.ic_tree // TODO: Replace
                "Cheat Tree" -> R.drawable.ic_tree // TODO: Replace
                else -> R.drawable.ic_tree // TODO: Replace
            }

            SkillMethodIcon(
                method = method,
                isSelected = isSelected,
                onMethodSelected = onMethodClicked,
                imageRes = imageRes,
                selectedBackgroundColor = wcSelectedBgColor,
                selectedIconTint = wcPrimaryColor,
                unselectedIconTint = wcUnselectedIconTint,
                selectionIndicatorColor = wcIndicatorColor
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WcTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod("Woodcutting", "Tree", 10, 10000),
            TrainingMethod("Woodcutting", "Oak", 15, 10000, 5),
            TrainingMethod("Woodcutting", "Willow Tree Tree Thisisatee", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Willow", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Willow 2", 30, 15000, 20),
            TrainingMethod("Woodcutting", "Willow 3", 30, 15000, 20)
        )

        WcTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[3],
            onMethodSelected = {}
        )
    }
}
