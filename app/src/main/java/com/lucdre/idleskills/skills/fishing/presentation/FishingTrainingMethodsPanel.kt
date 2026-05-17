package com.lucdre.idleskills.skills.fishing.presentation

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
 * Fishing-specific training methods panel. Uses [SkillTrainingPanel] and [SkillMethodIcon].
 *
 * @param modifier Modifier
 * @param methods List of available Fishing training methods
 * @param activeMethod Currently selected training method
 * @param activeCards Currently active cards for Fishing
 * @param trainingProgress Progress of the current Fishing action (0-1)
 * @param onMethodSelected Callback for when a training method is selected by the user
 */
@Composable
fun FishingTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeCards: List<Card> = emptyList(),
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    // Define fishing-specific visual parameters
    val fishingPrimaryColor = Color(0xFF0277BD)
    val fishingSelectedBgColor = Color(0xFFE1F5FE)
    val fishingIndicatorColor = Color(0xFF0288D1)
    val fishingUnselectedIconTint = Color(0xFF81D4FA)
    val fishingPanelBackgroundColor = Color(0x1A0277BD)

    SkillTrainingPanel(
        modifier = modifier,
        panelTitle = "Fish",
        panelBackgroundColor = fishingPanelBackgroundColor,
        primaryThemeColor = fishingPrimaryColor,
        methods = methods,
        activeMethod = activeMethod,
        activeCards = activeCards,
        trainingProgress = trainingProgress,
        onMethodSelected = onMethodSelected,
        methodItemContent = { method, isSelected, onMethodClicked ->
            val imageRes = when (method.name) {
                "Swordfish" -> R.drawable.ic_tree // TODO: Replace
                "Shark" -> R.drawable.ic_tree       // TODO: Replace
                else -> R.drawable.ic_tree        // TODO: Replace
            }
            SkillMethodIcon(
                method = method,
                isSelected = isSelected,
                onMethodSelected = onMethodClicked,
                imageRes = imageRes,
                selectedBackgroundColor = fishingSelectedBgColor,
                selectedIconTint = fishingPrimaryColor,
                unselectedIconTint = fishingUnselectedIconTint,
                selectionIndicatorColor = fishingIndicatorColor
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FishingTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod("Fishing", "Sardine", 10, 10000),
            TrainingMethod("Fishing", "Anchovy", 15, 10000, 5),
            TrainingMethod("Fishing", "Tuna", 30, 15000, 20),
            TrainingMethod("Fishing", "Lobster", 30, 15000, 20),
            TrainingMethod("Fishing", "Swordfish", 30, 15000, 20),
            TrainingMethod("Fishing", "Shark", 30, 15000, 20)
        )

        FishingTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[2],
            onMethodSelected = {}
        )
    }
}
