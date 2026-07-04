package com.lucdre.idleskills.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lucdre.idleskills.cards.presentation.CardViewModel
import com.lucdre.idleskills.main.presentation.TrainingSceneViewModel
import com.lucdre.idleskills.main.presentation.TrainingViewModel
import com.lucdre.idleskills.ui.screens.CardDetailScreen
import com.lucdre.idleskills.ui.screens.CardsScreen
import com.lucdre.idleskills.ui.screens.NewUserScreen
import com.lucdre.idleskills.ui.screens.InventoryScreen
import com.lucdre.idleskills.ui.screens.SettingsScreen
import com.lucdre.idleskills.ui.screens.StatsScreen
import com.lucdre.idleskills.ui.screens.trainingScreen.TrainingScreen
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.IdleSkillsPreviews

/**
 * Navigation routes.
 */
object Routes {
    const val TRAINING = "training"
    const val INVENTORY = "inventory"
    const val STATS = "stats"
    const val CARDS = "cards"
    const val SETTINGS = "settings"
    const val CARD_DETAIL = "card_detail"
}

/**
 * Navigation Item data class.
 *
 * @param title The title
 * @param selectedIcon
 * @param unselectedIcon
 * @param hasNews 'true' activates [Badge], 'false' doesn't
 * @param route
 */
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val route: String
)

/**
 * Main navigation container that manages bottom navigation screens.
 *
 * Checks if the game is fresh and shows initial skill selection screen accordingly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    viewModel: MainNavigationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Show loading or initial skill selection based on game state
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            return
        }
        uiState.isGameFresh -> {
            // Fresh game - show initial skill selection
            NewUserScreen(
                onSetupComplete = {
                    viewModel.onInitialSkillSelected()
                }
            )
            return
        }
        else -> {
            val trainingViewModel: TrainingViewModel = hiltViewModel()
            val sceneViewModel: TrainingSceneViewModel = hiltViewModel()
            val cardViewModel: CardViewModel = hiltViewModel()
            MainNavigationContent(
                trainingViewModel = trainingViewModel,
                sceneViewModel = sceneViewModel,
                cardViewModel = cardViewModel
            )
        }
    }
}

/**
 * Main navigation content with bottom navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavigationContent(
    trainingViewModel: TrainingViewModel,
    sceneViewModel: TrainingSceneViewModel,
    cardViewModel: CardViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navUiState by hiltViewModel<MainNavigationViewModel>().uiState.collectAsStateWithLifecycle()
    val adaptiveInfo = currentWindowAdaptiveInfo()

    val items = listOf(
        BottomNavigationItem(
            "Inventory",
            Icons.Filled.Inventory,
            Icons.Outlined.Inventory,
            navUiState.hasLootBoxes,
            Routes.INVENTORY
        ),
        BottomNavigationItem(
            "Stats",
            Icons.Filled.BarChart,
            Icons.Outlined.BarChart,
            false,
            Routes.STATS
        ),
        BottomNavigationItem(
            "Cards",
            Icons.Filled.Star,
            Icons.Outlined.Star,
            false,
            Routes.CARDS
        ),
        BottomNavigationItem(
            "Settings",
            Icons.Filled.Settings,
            Icons.Outlined.Settings,
            false,
            Routes.SETTINGS
        )
    )

    NavigationSuiteScaffold(
        modifier = Modifier.statusBarsPadding(),
        navigationSuiteItems = {
            items.forEach { item ->
                val selected = currentRoute == item.route
                item(
                    selected = selected,
                    onClick = {
                        if (selected) {
                            navController.navigate(Routes.TRAINING) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    label = { Text(text = item.title) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.hasNews) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selected) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.TRAINING,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.TRAINING) {
                val skillsState by trainingViewModel.skillsState.collectAsStateWithLifecycle()
                val sceneState by sceneViewModel.uiState.collectAsStateWithLifecycle()
                val sessionState by trainingViewModel.sessionState.collectAsStateWithLifecycle()
                val activeStateState = trainingViewModel.activeTrainingState.collectAsStateWithLifecycle()

                androidx.compose.runtime.LaunchedEffect(skillsState.activeTrainingSkill) {
                    sceneViewModel.updateSpawningStatus(
                        visible = true,
                        isTraining = skillsState.activeTrainingSkill != null
                    )
                }
                
                TrainingScreen(
                    skillsState = skillsState,
                    sceneState = sceneState,
                    sessionState = sessionState,
                    activeStateProvider = { activeStateState.value },
                    onSkillSelect = { skill -> trainingViewModel.selectSkill(skill) },
                    onMethodSelect = { method -> trainingViewModel.selectTrainingMethod(method) },
                    onRegionClick = { /* Handle region change */ },
                    onSpriteClick = { 
                        sceneViewModel.onSpriteClick()
                    },
                    onDismissOfflineProgress = { trainingViewModel.dismissOfflineProgress() },
                    onSetScreenVisible = { isVisible -> 
                        trainingViewModel.setScreenVisible(isVisible)
                        sceneViewModel.updateSpawningStatus(
                            visible = isVisible, 
                            isTraining = skillsState.activeTrainingSkill != null
                        )
                    },
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    sceneViewModel = sceneViewModel
                )
            }
            composable(Routes.INVENTORY) {
                InventoryScreen()
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(Routes.CARDS) {
                CardsScreen(
                    viewModel = cardViewModel,
                    navController = navController
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
            composable("${Routes.CARD_DETAIL}/{cardName}") { backStackEntry ->
                val cardName = backStackEntry.arguments?.getString("cardName")
                val uiState by cardViewModel.uiState.collectAsStateWithLifecycle()
                val cardState = uiState.cardsBySkill.values.flatten().find { it.card.name == cardName }
                
                if (cardState != null) {
                    CardDetailScreen(
                        cardState = cardState,
                        viewModel = cardViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
@IdleSkillsPreviews
fun MainNavigationPreview() {
    IdleSkillsTheme {
        Column {
            Text(
                text = "Navigation Preview",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )

            NavigationBar {
                val items = listOf("Inventory", "Stats", "Cards", "Settings")
                val icons = listOf(
                    Icons.Filled.Inventory,
                    Icons.Filled.BarChart,
                    Icons.Filled.Star,
                    Icons.Filled.Settings
                )

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == -1, // None selected for preview of background state
                        onClick = { },
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) }
                    )
                }
            }
        }
    }
}
