package com.lucdre.idleskills.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucdre.idleskills.profile.presentation.InitialSkillSelectionViewModel
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillTheme
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

/**
 * Screen for initial setup: entering username and selecting a favorite skill.
 */
@Composable
fun InitialSkillSelectionScreen(
    modifier: Modifier = Modifier,
    onSetupComplete: () -> Unit,
    viewModel: InitialSkillSelectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isProfileSetupComplete) {
        if (uiState.isProfileSetupComplete) {
            onSetupComplete()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        InitialSkillSelectionContent(
            username = username,
            onUsernameChange = { username = it },
            onStartClick = {
                viewModel.setupProfile(username)
            },
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage
        )
    }
}

@Composable
private fun InitialSkillSelectionContent(
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
            isError = username.isBlank()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

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
fun InitialSkillSelectionScreenPreview() {
    IdleSkillsTheme {
        InitialSkillSelectionContent(
            username = "Player One",
            onUsernameChange = {},
            onStartClick = {},
            isLoading = false,
            errorMessage = null
        )
    }
}
