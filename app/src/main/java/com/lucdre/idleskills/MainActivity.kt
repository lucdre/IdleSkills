package com.lucdre.idleskills

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lucdre.idleskills.ui.navigation.MainNavigation
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
                MainNavigation()
            }
        }
    }
}