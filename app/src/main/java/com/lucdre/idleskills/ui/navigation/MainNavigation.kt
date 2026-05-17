package com.lucdre.idleskills.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.screens.CardsScreen
import com.lucdre.idleskills.ui.screens.InitialSkillSelectionScreen
import com.lucdre.idleskills.ui.screens.QuestsScreen
import com.lucdre.idleskills.ui.screens.SettingsScreen
import com.lucdre.idleskills.ui.screens.SkillListScreen
import com.lucdre.idleskills.ui.screens.StatsScreen
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Navigation Item data class.
 *
 * @param title The title
 * @param selectedIcon
 * @param unselectedIcon
 * @param hasNews 'true' activates [Badge], 'false' doesn't
 */
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
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
            // Loading state - show a simple loading screen
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
            InitialSkillSelectionScreen(
                onSetupComplete = {
                    viewModel.onInitialSkillSelected()
                }
            )
            return
        }
        else -> {
            // Normal game flow
            val skillListViewModel: SkillListViewModel = hiltViewModel()
            MainNavigationContent(skillListViewModel = skillListViewModel)
        }
    }
}

/**
 * Main navigation content with bottom navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavigationContent(
    skillListViewModel: SkillListViewModel
) {
    // TODO compose state items to be able to change hasNews
    val items = listOf(
        BottomNavigationItem(
            "Skills",
            Icons.Filled.Home,
            Icons.Outlined.Home,
            false
        ),
        BottomNavigationItem(
            "Stats",
            Icons.Filled.BarChart,
            Icons.Outlined.BarChart,
            false
        ),
        BottomNavigationItem(
            "Quests",
            Icons.Filled.CheckCircle,
            Icons.Outlined.CheckCircle,
            false
        ),
        BottomNavigationItem(
            "Cards",
            Icons.Filled.Star,
            Icons.Outlined.Star,
            false
        ),
        BottomNavigationItem(
            "Settings",
            Icons.Filled.Settings,
            Icons.Outlined.Settings,
            false
        )
    )

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        label = {
                            Text(text = item.title)
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (index == selectedTabIndex) {
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
        }
    ) { innerPadding ->
        // Switch screens based on selected tab
        when (selectedTabIndex) {
            0 -> {
                SkillListScreen(
                    skillViewModel = skillListViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            1 -> StatsScreen(
                modifier = Modifier.padding(innerPadding),
                skillViewModel = skillListViewModel
            )
            2 -> QuestsScreen(modifier = Modifier.padding(innerPadding))
            3 -> CardsScreen(modifier = Modifier.padding(innerPadding))
            4 -> SettingsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainNavigationPreview() {
    IdleSkillsTheme {
        Column {
            Text(
                text = "Navigation Preview",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )

            NavigationBar {
                val items = listOf("Skills", "Stats", "Quests", "Cards", "Settings")
                val icons = listOf(
                    Icons.Filled.Home,
                    Icons.Filled.BarChart,
                    Icons.Filled.CheckCircle,
                    Icons.Filled.Star,
                    Icons.Filled.Settings
                )

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == 2,
                        onClick = { },
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) }
                    )
                }
            }
        }
    }
}
