package com.lucdre.idleskills.skills.mining.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.tools.Tool
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
 * @param activeTool Currently selected Mining tool
 * @param hasBetterToolAvailable Whether a better tool is available
 * @param trainingProgress Progress of the current Mining action (0-1)
 * @param onMethodSelected Callback for when a training method is selected by the user
 * @param onToolSelected Callback for when a tool is selected by the user
 */
@Composable
fun MiningTrainingMethodsPanel(
    modifier: Modifier = Modifier,
    methods: List<TrainingMethod>,
    activeMethod: TrainingMethod?,
    activeTool: Tool?,
    hasBetterToolAvailable: Boolean = false,
    trainingProgress: Float = 0f,
    onMethodSelected: (TrainingMethod) -> Unit,
    onToolSelected: (Tool) -> Unit
) {
    // Define Mining-specific visual parameters
    val miningPrimaryColor = Color(0xff5a5a5a)
    val miningSelectedBgColor = Color(0xFFDCDCDC)
    val miningIndicatorColor =  Color(0xff5a5a5a)
    val miningUnselectedIconTint = Color.LightGray
    val miningPanelBackgroundColor = Color.LightGray//Color(0xFFA9ABAB)

    SkillTrainingPanel(
        modifier = modifier,
        panelTitle = "Rocks",
        panelBackgroundColor = miningPanelBackgroundColor,
        primaryThemeColor = miningPrimaryColor,
        defaultToolIconRes = R.drawable.ic_tree, // TODO: Replace
        methods = methods,
        activeMethod = activeMethod,
        activeTool = activeTool,
        hasBetterToolAvailable = hasBetterToolAvailable,
        trainingProgress = trainingProgress,
        onMethodSelected = onMethodSelected,
        onToolSelected = onToolSelected,
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

        val tool = Tool("Mining", "Iron Pickaxe", 1.2f, 5, R.drawable.ic_tree)

        MiningTrainingMethodsPanel(
            methods = methods,
            activeMethod = methods[2],
            activeTool = tool,
            hasBetterToolAvailable = true,
            onMethodSelected = {},
            onToolSelected = {}
        )
    }
}
