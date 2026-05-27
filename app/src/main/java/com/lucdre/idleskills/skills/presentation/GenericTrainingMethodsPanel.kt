package com.lucdre.idleskills.skills.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * A generic training methods panel that adapts its visuals based on the skill metadata.
 *
 * @param modifier Modifier for this composable.
 * @param skillName Name of the skill to display methods for.
 * @param methods List of available training methods.
 * @param activeMethod Currently selected training method.
 * @param activeCards Currently active cards for the skill.
 * @param trainingProgress Progress of the current training action (0-1).
 * @param onMethodSelected Callback for when a training method is selected.
 */
@Composable
fun GenericTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    skillName: String,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeCards: List<Card> = emptyList(),
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit
) {
    val theme = SkillMetadata.getTheme(skillName)
    
    // Determine panel title based on skill
    val panelTitle = when (skillName.lowercase()) {
        "woodcutting" -> "Trees"
        "mining" -> "Rocks"
        "fishing" -> "Fish"
        else -> skillName
    }

    SkillTrainingPanel(
        modifier = modifier,
        panelTitle = panelTitle,
        panelBackgroundColor = theme.panelBackgroundColor,
        primaryThemeColor = theme.primaryColor,
        methods = methods,
        activeMethod = activeMethod,
        activeCards = activeCards,
        trainingProgress = trainingProgress,
        onMethodSelected = onMethodSelected,
        methodItemContent = { method, isSelected, onMethodClicked ->
            val imageRes = SkillMetadata.getMethodIcon(skillName, method.name)
            
            SkillMethodIcon(
                method = method,
                isSelected = isSelected,
                onMethodSelected = onMethodClicked,
                imageRes = imageRes,
                selectedBackgroundColor = theme.selectedBgColor,
                selectedIconTint = theme.primaryColor,
                unselectedIconTint = theme.unselectedIconTint,
                selectionIndicatorColor = theme.indicatorColor
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WoodcuttingTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod(SkillType.WOODCUTTING, "Tree", 10, 3000, requiredCardType = CardType.WOODCUTTING_AXE),
            TrainingMethod(SkillType.WOODCUTTING, "Oak Tree", 15, 4000, 10, requiredCardType = CardType.WOODCUTTING_AXE),
            TrainingMethod(SkillType.WOODCUTTING, "Willow Tree", 22, 5000, 25, requiredCardType = CardType.WOODCUTTING_AXE)
        )

        GenericTrainingMethodsPanel(
            skillName = "Woodcutting",
            methods = methods,
            activeMethod = methods[1],
            onMethodSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MiningTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod(SkillType.MINING, "Copper Rock", 10, 3000, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod(SkillType.MINING, "Tin Rock", 10, 3000, requiredCardType = CardType.MINING_PICKAXE),
            TrainingMethod(SkillType.MINING, "Iron Rock", 20, 4000, 5, requiredCardType = CardType.MINING_PICKAXE)
        )

        GenericTrainingMethodsPanel(
            skillName = "Mining",
            methods = methods,
            activeMethod = methods[0],
            onMethodSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FishingTrainingMethodsPanelPreview() {
    IdleSkillsTheme {
        val methods = listOf(
            TrainingMethod(SkillType.FISHING, "Sardine", 10, 3000, requiredCardType = CardType.FISHING_NET),
            TrainingMethod(SkillType.FISHING, "Trout", 20, 4000, 5, requiredCardType = CardType.FISHING_ROD),
            TrainingMethod(SkillType.FISHING, "Shark", 350, 30000, 80, requiredCardType = CardType.FISHING_HARPOON)
        )

        GenericTrainingMethodsPanel(
            skillName = "Fishing",
            methods = methods,
            activeMethod = methods[1],
            onMethodSelected = {}
        )
    }
}
