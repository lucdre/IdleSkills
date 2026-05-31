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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.main.presentation.ActiveTrainingState
import com.lucdre.idleskills.main.presentation.TrainingLootState
import com.lucdre.idleskills.main.presentation.TrainingSessionState
import com.lucdre.idleskills.main.presentation.TrainingSkillsState
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.ui.components.OfflineProgressPopup
import com.lucdre.idleskills.ui.screens.trainingScreen.components.*
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.NumberFormatter

@Composable
fun TrainingScreen(
    skillsState: TrainingSkillsState,
    lootState: TrainingLootState,
    sessionState: TrainingSessionState,
    activeStateProvider: () -> ActiveTrainingState,
    onSkillSelect: (SkillType) -> Unit,
    onMethodSelect: (String) -> Unit,
    onRegionClick: () -> Unit,
    onSpriteClick: () -> Unit,
    onDismissOfflineProgress: () -> Unit,
    onSetScreenVisible: (Boolean) -> Unit = {}
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Game Scene - Volatile (reads activeStateProvider)
            item {
                TrainingSceneCard(
                    regionName = sessionState.regionName,
                    activeSkill = skillsState.activeTrainingSkill,
                    methodName = skillsState.activeTrainingMethod?.name,
                    progressProvider = { activeStateProvider().trainingProgress },
                    onRegionClick = onRegionClick
                )
            }

            // Skill Selection - Stable
            item {
                SkillSelector(
                    skills = SkillType.entries,
                    selectedSkill = skillsState.expandedSkillName?.let { SkillType.fromString(it) },
                    onSkillSelected = onSkillSelect
                )
            }

            // Training Method Selection - Stable
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
                        selected = skillsState.activeTrainingMethod?.name == method.name,
                        onClick = { onMethodSelect(method.name) }
                    )
                }
            }

            // Training Info / Stats - Mixed
            if (skillsState.activeTrainingSkill != null) {
                item {
                    TrainingStatsSection(
                        skillsState = skillsState,
                        activeStateProvider = activeStateProvider
                    )
                }
            }
        }

        // Random Loot Sprite
        if (lootState.isSpriteVisible) {
            LootSpriteOverlay(
                position = lootState.spritePosition,
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
        
        // Stats Grid - Volatile (reads activeStateProvider)
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

@Preview(showBackground = true, backgroundColor = 0xFF08111C)
@Composable
fun TrainingScreenPreview() {
    IdleSkillsTheme {
        val skillsState = TrainingSkillsState(
            activeTrainingSkill = SkillType.WOODCUTTING,
            expandedSkillName = "Woodcutting",
            trainingMethods = listOf(
                com.lucdre.idleskills.skills.domain.training.TrainingMethod(
                    SkillType.WOODCUTTING, "Normal Trees", 25, 5000
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
            lootState = TrainingLootState(),
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
