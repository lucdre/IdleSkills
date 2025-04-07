package com.lucdre.idleskills.skills.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lucdre.idleskills.skills.domain.TrainingMethod
import com.lucdre.idleskills.skills.woodcutting.presentation.WcTrainingMethodsPanel

/**
 * Factory to provide skill-specific training method panels
 */
object TrainingMethodsPanelFactory {

    @Composable
    fun CreateTrainingMethodsPanel(
        skillName: String,
        methods: List<TrainingMethod>,
        activeMethod: TrainingMethod?,
        trainingProgress: Float = 0f,  // Add this new parameter
        onMethodSelected: (TrainingMethod) -> Unit,
        modifier: Modifier = Modifier
    ) {
        when (skillName.lowercase()) {
            "woodcutting" -> WcTrainingMethodsPanel(
                methods = methods,
                activeMethod = activeMethod,
                trainingProgress = trainingProgress,  // Pass the parameter
                onMethodSelected = onMethodSelected,
                modifier = modifier
            )
            // Add cases for other skills as they're implemented //TODO

            // Default panel as fallback
            else -> DefaultTrainingMethodsPanel(
                methods = methods,
                activeMethod = activeMethod,
                trainingProgress = trainingProgress,  // Pass the parameter
                onMethodSelected = onMethodSelected,
                modifier = modifier
            )
        }
    }
}