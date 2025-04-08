package com.lucdre.idleskills

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucdre.idleskills.skills.presentation.SkillListScreen
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the UI.
 *
 * Activity hosts the skill list screen as main interface.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IdleSkillsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Get ViewModel via Hilt
                    val viewModel: SkillListViewModel = hiltViewModel()

                    // Display the SkillListScreen
                    SkillListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}