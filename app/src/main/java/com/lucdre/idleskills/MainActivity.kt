package com.lucdre.idleskills

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.lucdre.idleskills.main.presentation.TrainingViewModel
import com.lucdre.idleskills.ui.navigation.MainNavigation
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val trainingViewModel: TrainingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            IdleSkillsTheme {
                MainNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        trainingViewModel.setAppVisibility(true)
    }

    override fun onStop() {
        super.onStop()
        trainingViewModel.setAppVisibility(false)
    }
}
