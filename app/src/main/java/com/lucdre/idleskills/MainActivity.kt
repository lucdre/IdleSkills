package com.lucdre.idleskills

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.compose.runtime.CompositionLocalProvider
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.main.presentation.TrainingViewModel
import com.lucdre.idleskills.ui.navigation.MainNavigation
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import com.lucdre.idleskills.ui.util.LocalItemRegistry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Entry point.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val trainingViewModel: TrainingViewModel by viewModels()

    @Inject
    lateinit var itemRegistry: ItemRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(trainingViewModel)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalItemRegistry provides itemRegistry) {
                IdleSkillsTheme {
                    MainNavigation()
                }
            }
        }
    }
}
