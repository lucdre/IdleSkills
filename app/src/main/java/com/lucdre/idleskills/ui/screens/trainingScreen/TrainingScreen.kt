package com.lucdre.idleskills.ui.screens.trainingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.lucdre.idleskills.main.presentation.ActiveTrainingState
import com.lucdre.idleskills.main.presentation.TrainingSceneState
import com.lucdre.idleskills.main.presentation.TrainingSessionState
import com.lucdre.idleskills.main.presentation.TrainingSkillsState
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import com.lucdre.idleskills.ui.components.OfflineProgressPopup
import com.lucdre.idleskills.ui.screens.trainingScreen.components.*
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
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
    windowSizeClass: WindowSizeClass? = null
) {
    androidx.compose.runtime.DisposableEffect(Unit) {
        onSetScreenVisible(true)
        onDispose {
            onSetScreenVisible(false)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = windowSizeClass != null && 
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

        if (isWideScreen) {
            TwoColumnTrainingLayout(
                skillsState = skillsState,
                sceneState = sceneState,
                sessionState = sessionState,
                activeStateProvider = activeStateProvider,
                onSkillSelect = onSkillSelect,
                onMethodSelect = onMethodSelect,
                onRegionClick = onRegionClick
            )
        } else {
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
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            TrainingSceneCard(
                regionName = sessionState.regionName,
                activeSkill = skillsState.activeTrainingSkill,
                methodType = skillsState.activeTrainingMethod?.type,
                progressProvider = { activeStateProvider().trainingProgress },
                onRegionClick = onRegionClick
            )
        }

        item {
            SkillSelector(
                skills = SkillType.entries,
                selectedSkill = skillsState.expandedSkillName?.let { SkillType.fromString(it) },
                onSkillSelected = onSkillSelect
            )
        }

        if (skillsState.trainingMethods.isNotEmpty()) {
            item {
                Text(
                    text = "Training Method",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(skillsState.trainingMethods) { method ->
                TrainingMethodCard(
                    method = method,
                    selected = skillsState.activeTrainingMethod?.type == method.type,
                    onClick = { onMethodSelect(method.type) }
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
private fun TwoColumnTrainingLayout(
    skillsState: TrainingSkillsState,
    sceneState: TrainingSceneState,
    sessionState: TrainingSessionState,
    activeStateProvider: () -> ActiveTrainingState,
    onSkillSelect: (SkillType) -> Unit,
    onMethodSelect: (TrainingMethodType) -> Unit,
    onRegionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column: Scene and Stats
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TrainingSceneCard(
                    regionName = sessionState.regionName,
                    activeSkill = skillsState.activeTrainingSkill,
                    methodType = skillsState.activeTrainingMethod?.type,
                    progressProvider = { activeStateProvider().trainingProgress },
                    onRegionClick = onRegionClick
                )
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

        // Right Column: Skill and Method Selection
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SkillSelector(
                    skills = SkillType.entries,
                    selectedSkill = skillsState.expandedSkillName?.let { SkillType.fromString(it) },
                    onSkillSelected = onSkillSelect
                )
            }

            if (skillsState.trainingMethods.isNotEmpty()) {
                item {
                    Text(
                        text = "Training Method",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground, //TODO put this item on the card as well
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(skillsState.trainingMethods) { method ->
                    TrainingMethodCard(
                        method = method,
                        selected = skillsState.activeTrainingMethod?.type == method.type,
                        onClick = { onMethodSelect(method.type) }
                    )
                }
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
                label = "XP Per Hour",
                valueProvider = { NumberFormatter.formatNumber(activeStateProvider().xpPerHour) },
                icon = Icons.Default.Schedule
            )

            Spacer(modifier = Modifier.width(8.dp))

            StatsCard(
                modifier = Modifier.weight(1f),
                label = "Time to Level Up",
                valueProvider = { NumberFormatter.formatDuration(activeStateProvider().timeToLevelUpMs) },
                icon = Icons.Default.Timer
            )

            Spacer(modifier = Modifier.width(8.dp))

            StatsCard(
                modifier = Modifier.weight(1f),
                label = "XP gained",
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
                )
            )
        )
        
        val activeState = ActiveTrainingState(
            trainingProgress = 0.6f,
            sessionXpGained = 12345,
            xpPerHour = 15420,
            timeToLevelUpMs = 619000,
        )

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
