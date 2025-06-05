package com.lucdre.idleskills.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucdre.idleskills.ui.screens.SkillListScreen
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.screens.AchievementsScreen
import com.lucdre.idleskills.ui.screens.QuestsScreen
import com.lucdre.idleskills.ui.screens.SettingsScreen
import com.lucdre.idleskills.ui.screens.ToolsScreen
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    // TODO compose state items to be able to change hasNews
    val items = listOf(
        BottomNavigationItem(
            "Skills",
            Icons.Filled.Home,
            Icons.Outlined.Home,
            false
        ),
        BottomNavigationItem(
            "Tools",
            Icons.Filled.Build,
            Icons.Outlined.Build,
            false
        ),
        BottomNavigationItem(
            "Quests",
            Icons.Filled.CheckCircle,
            Icons.Outlined.CheckCircle,
            false
        ),
        BottomNavigationItem(
            "Goals",
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
                                    imageVector = if(index == selectedTabIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
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
                val skillViewModel: SkillListViewModel = hiltViewModel()
                SkillListScreen(
                    skillViewModel = skillViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            1 -> ToolsScreen(modifier = Modifier.padding(innerPadding))
            2 -> QuestsScreen(modifier = Modifier.padding(innerPadding))
            3 -> AchievementsScreen(modifier = Modifier.padding(innerPadding))
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
                val items = listOf("Skills", "Tools", "Quests", "Goals", "Settings")
                val icons = listOf(
                    Icons.Filled.Home,
                    Icons.Filled.Build,
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