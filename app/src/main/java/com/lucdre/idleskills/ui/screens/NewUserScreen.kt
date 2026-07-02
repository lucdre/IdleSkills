package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.profile.presentation.NewUserViewModel
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen for initial setup.
 */
@Composable
fun NewUserScreen(
    modifier: Modifier = Modifier,
    onSetupComplete: () -> Unit,
    viewModel: NewUserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewUserViewModel.Effect.NavigateToMain -> {
                    onSetupComplete()
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NewUserScreenContent(
            username = username,
            onUsernameChange = { 
                username = it
                viewModel.clearError()
            },
            onStartClick = {
                viewModel.setupProfile(username)
            },
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage
        )
    }
}

@Composable
private fun NewUserScreenContent(
    modifier: Modifier = Modifier,
    username: String,
    onUsernameChange: (String) -> Unit,
    onStartClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to IdleSkills!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Enter your username to begin:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null,
            supportingText = {
                if (errorMessage != null) {
                    Text(text = errorMessage)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading && username.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("START JOURNEY", fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewUserScreenScreenPreview() {
    IdleSkillsTheme {
        NewUserScreenContent(
            username = "Player One",
            onUsernameChange = {},
            onStartClick = {},
            isLoading = false,
            errorMessage = null
        )
    }
}
