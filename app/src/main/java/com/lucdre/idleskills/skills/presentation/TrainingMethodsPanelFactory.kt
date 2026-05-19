package com.lucdre.idleskills.skills.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.skills.domain.training.TrainingMethod

/**
 * Factory to provide skill-specific training method panels.
 *
 * Uses a generic implementation that adapts based on SkillMetadata.
 */
object TrainingMethodsPanelFactory {

    /**
     * Creates a training methods panel for the specified skill.
     *
     * @param modifier Modifier.
     * @param skillName Name of the skill to create a panel for.
     * @param methods List of training methods available for the skill.
     * @param activeMethod Currently selected training method.
     * @param trainingProgress Progress of the current training action (0-1).
     * @param activeCards Currently active cards for the skill.
     * @param onMethodSelected Callback for when a training method is selected.
     */
    @Composable
    fun CreateTrainingMethodsPanel(
        modifier: Modifier = Modifier,
        skillName: String,
        methods: List<TrainingMethod>,
        activeMethod: TrainingMethod?,
        trainingProgress: Float = 0f,
        activeCards: List<Card> = emptyList(),
        onMethodSelected: (TrainingMethod) -> Unit
    ) {
        GenericTrainingMethodsPanel(
            modifier = modifier,
            skillName = skillName,
            methods = methods,
            activeMethod = activeMethod,
            trainingProgress = trainingProgress,
            activeCards = activeCards,
            onMethodSelected = onMethodSelected
        )
    }
}
