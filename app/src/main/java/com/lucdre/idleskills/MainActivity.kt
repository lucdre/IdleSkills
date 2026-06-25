package com.lucdre.idleskills

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ProcessLifecycleOwner
import com.lucdre.idleskills.main.presentation.TrainingViewModel
import com.lucdre.idleskills.ui.navigation.MainNavigation
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Entry point.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val trainingViewModel: TrainingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(trainingViewModel)

        enableEdgeToEdge()
        setContent {
            IdleSkillsTheme {
                MainNavigation()
            }
        }
    }
}
