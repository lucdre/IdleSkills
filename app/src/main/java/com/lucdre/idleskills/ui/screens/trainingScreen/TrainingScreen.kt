package com.lucdre.idleskills.ui.screens.trainingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.main.presentation.ActiveTrainingState
import com.lucdre.idleskills.main.presentation.TrainingSceneState
import com.lucdre.idleskills.main.presentation.TrainingSceneUiEffect
import com.lucdre.idleskills.main.presentation.TrainingSceneViewModel
import com.lucdre.idleskills.main.presentation.TrainingSessionState
import com.lucdre.idleskills.main.presentation.TrainingSkillsState
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import com.lucdre.idleskills.ui.components.OfflineProgressPopup
import com.lucdre.idleskills.ui.screens.trainingScreen.components.LevelProgressCard
import com.lucdre.idleskills.ui.screens.trainingScreen.components.LootSpriteOverlay
import com.lucdre.idleskills.ui.screens.trainingScreen.components.SkillSelector
import com.lucdre.idleskills.ui.screens.trainingScreen.components.StatsCard
import com.lucdre.idleskills.ui.screens.trainingScreen.components.TrainingMethodSelector
import com.lucdre.idleskills.ui.screens.trainingScreen.components.TrainingSceneCard
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.theme.Spacing
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews
import com.lucdre.idleskills.ui.util.LocalItemRegistry
import com.lucdre.idleskills.ui.util.NumberFormatter

@Composable
fun TrainingScreen(
    skillsState: TrainingSkillsState,
    sceneState: TrainingSceneState,
    sessionState: TrainingSessionState,
    activeStateProvider: () -> ActiveTrainingState,
    onSkillSelect: (SkillType) -> Unit,
    onMethodSelect: (TrainingMethodType) -> Unit,
    onRegionClick: () -> Unit,
    onSpriteClick: () -> Unit,
    onDismissOfflineProgress: () -> Unit,
    onSetScreenVisible: (Boolean) -> Unit = {},
    windowSizeClass: WindowSizeClass? = null,
    sceneViewModel: TrainingSceneViewModel? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(sceneViewModel) {
        onSetScreenVisible(true)
        sceneViewModel?.uiEffects?.collect { effect ->
            when (effect) {
                is TrainingSceneUiEffect.ShowLootMessage -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onSetScreenVisible(false)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
        ) {
            SingleColumnTrainingLayout(
                skillsState = skillsState,
                sceneState = sceneState,
                sessionState = sessionState,
                activeStateProvider = activeStateProvider,
                onSkillSelect = onSkillSelect,
                onMethodSelect = onMethodSelect,
                onRegionClick = onRegionClick
            )
        }

        // Random Loot Sprite
        if (sceneState.isSpriteVisible) {
            LootSpriteOverlay(
                position = sceneState.spritePosition,
                onSpriteClick = onSpriteClick
            )
        }

        // Offline Progress Popup
        sessionState.offlineProgress?.let { result ->
            OfflineProgressPopup(
                result = result,
                onDismiss = onDismissOfflineProgress
            )
        }
    }
}


@Composable
fun rememberTickingDuration(baseDurationMs: Long): Long {
    val startTime = remember(baseDurationMs) { System.currentTimeMillis() }
    var currentTime by remember(baseDurationMs) { mutableStateOf(startTime) }

    LaunchedEffect(baseDurationMs) {
        while (isActive) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val elapsed = currentTime - startTime
    return maxOf(0L, baseDurationMs - elapsed)
}

@Composable
private fun SingleColumnTrainingLayout(
    skillsState: TrainingSkillsState,
    sceneState: TrainingSceneState,
    sessionState: TrainingSessionState,
    activeStateProvider: () -> ActiveTrainingState,
    onSkillSelect: (SkillType) -> Unit,
    onMethodSelect: (TrainingMethodType) -> Unit,
    onRegionClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(Spacing.ScreenEdge)
    ) {
        item {
            val activeState = activeStateProvider()
            TrainingSceneCard(
                regionName = sessionState.regionName,
                activeSkill = skillsState.activeTrainingSkill,
                methodType = skillsState.activeTrainingMethod?.type,
                startTime = activeState.startTime,
                durationMs = activeState.durationMs,
                onRegionClick = onRegionClick
            )
        }

        item {
            SkillSelector(
                skills = SkillType.entries,
                selectedSkill = skillsState.expandedSkillName?.let { SkillType.fromString(it) },
                activeSkill = skillsState.activeTrainingSkill,
                onSkillSelected = onSkillSelect
            )
        }

        if (skillsState.trainingMethods.isNotEmpty()) {
            item {
                TrainingMethodSelector(
                    methods = skillsState.trainingMethods,
                    selectedMethodType = skillsState.activeTrainingMethod?.type,
                    onMethodSelected = onMethodSelect
                )
            }
        }

        if (skillsState.activeTrainingSkill != null) {
            item {
                TrainingStatsSection(
                    skillsState = skillsState,
                    activeStateProvider = activeStateProvider
                )
            }
        }
    }
}


@Composable
fun TrainingStatsSection(
    skillsState: TrainingSkillsState,
    activeStateProvider: () -> ActiveTrainingState
) {
    Column {
        Text(
            text = "Training Info",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Level Progress Card - Stable
        LevelProgressCard(
            level = skillsState.levelInfo.currentLevel,
            totalXp = skillsState.levelInfo.totalXp,
            nextLevelXp = skillsState.levelInfo.nextLevelXp,
            xpToNextLevel = skillsState.levelInfo.xpToNextLevel,
            progress = skillsState.levelInfo.progressDecimal
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Stats Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            StatsCard(
                modifier = Modifier.weight(1f),
                label = "XP/h",
                valueProvider = { NumberFormatter.formatNumber(activeStateProvider().xpPerHour) },
                icon = Icons.Default.Schedule
            )

            Spacer(modifier = Modifier.width(8.dp))

            val staticTime = activeStateProvider().timeToLevelUpMs
            val tickingTime = rememberTickingDuration(baseDurationMs = staticTime)

            StatsCard(
                modifier = Modifier.weight(1f),
                label = "Level " + (skillsState.levelInfo.currentLevel + 1).toString() + " in",
                valueProvider = { NumberFormatter.formatDuration(tickingTime) },
                icon = Icons.Default.KeyboardDoubleArrowUp
            )

            Spacer(modifier = Modifier.width(8.dp))

            StatsCard(
                modifier = Modifier.weight(1f),
                label = "Session XP",
                valueProvider = { NumberFormatter.formatNumber(activeStateProvider().sessionXpGained) },
                icon = Icons.Default.Timer
            )
        }
    }
}

@IdleSkillsPreviews
@Composable
fun TrainingScreenPreview() {
    IdleSkillsTheme {
        val skillsState = TrainingSkillsState(
            activeTrainingSkill = SkillType.WOODCUTTING,
            expandedSkillName = "Woodcutting",
            trainingMethods = listOf(
                com.lucdre.idleskills.skills.domain.training.TrainingMethod(
                    type = TrainingMethodType.WC_TREE,
                    xpPerAction = 25,
                    actionDurationMs = 5000
                ),
                com.lucdre.idleskills.skills.domain.training.TrainingMethod(
                    type = TrainingMethodType.WC_TREE,
                    xpPerAction = 25,
                    actionDurationMs = 5000
                )
            )
        )
        
        val activeState = ActiveTrainingState(
            startTime = System.currentTimeMillis() - 3000,
            durationMs = 5000,
            sessionXpGained = 12345,
            xpPerHour = 15420,
            timeToLevelUpMs = 619000,
        )

        CompositionLocalProvider(
            LocalItemRegistry provides ItemRegistry()
        ) {
            TrainingScreen(
                skillsState = skillsState,
                sceneState = TrainingSceneState(),
                sessionState = TrainingSessionState(regionName = "Region 1"),
                activeStateProvider = { activeState },
                onSkillSelect = {},
                onMethodSelect = {},
                onRegionClick = {},
                onSpriteClick = {},
                onDismissOfflineProgress = {}
            )
        }
    }
}
