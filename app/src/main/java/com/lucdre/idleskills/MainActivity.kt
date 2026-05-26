package com.lucdre.idleskills

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.lucdre.idleskills.core.domain.OfflineProgressManager
import com.lucdre.idleskills.skills.presentation.SkillListViewModel
import com.lucdre.idleskills.ui.navigation.MainNavigation
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main entry point for the UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var offlineProgressManager: OfflineProgressManager

    private val skillViewModel: SkillListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Trigger offline progress calculation on startup
        lifecycleScope.launch {
            val result = offlineProgressManager.calculateAndApplyOfflineProgress()
            if (result != null) {
                skillViewModel.setOfflineProgress(result)
            }
        }

        enableEdgeToEdge()
        setContent {
            IdleSkillsTheme {
                MainNavigation()
            }
        }
    }
}