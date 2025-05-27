package com.lucdre.idleskills.skills.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.training.TrainingMethod
import com.lucdre.idleskills.skills.woodcutting.presentation.WcTrainingMethodsPanel

/**
 * Factory to provide skill-specific training method panels.
 * 
 * Uses a factory pattern to create the appropriate training methods panel
 * based on the skill type, with a default implementation as fallback while more methods are added.
 */
object TrainingMethodsPanelFactory {

    /**
     * Creates a skill-specific training methods panel.
     *
     * @param modifier Modifier
     * @param skillName Name of the skill to create a panel for
     * @param methods List of training methods available for the skill
     * @param activeMethod Currently selected training method
     * @param trainingProgress Progress of the current training action (0-1)
     * @param activeTool Currently selected tool
     * @param hasBetterToolAvailable Whether a better tool is available for the skill
     * @param onMethodSelected Callback for when a training method is selected
     * @param onToolSelected Callback for when a better tool is selected
     */
    @Composable
    fun CreateTrainingMethodsPanel(
        modifier: Modifier = Modifier,
        skillName: String,
        methods: List<TrainingMethod>,
        activeMethod: TrainingMethod?,
        trainingProgress: Float = 0f,
        activeTool: Tool?,
        hasBetterToolAvailable: Boolean = false,
        onMethodSelected: (TrainingMethod) -> Unit,
        onToolSelected: () -> Unit
    ) {
        when (skillName.lowercase()) {
            "woodcutting" -> WcTrainingMethodsPanel(
                modifier = modifier,
                methods = methods,
                activeMethod = activeMethod,
                trainingProgress = trainingProgress,
                activeTool = activeTool,
                hasBetterToolAvailable = hasBetterToolAvailable,
                onMethodSelected = onMethodSelected,
                onToolSelected = { onToolSelected() }
            )
            // Add cases for other skills as they're implemented //TODO

            // Default panel as fallback
            else -> DefaultTrainingMethodsPanel(
                modifier = modifier,
                methods = methods,
                activeMethod = activeMethod,
                trainingProgress = trainingProgress,
                onMethodSelected = onMethodSelected
            )
        }
    }
}