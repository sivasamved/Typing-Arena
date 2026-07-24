package com.typingarena.ui.screens

import androidx.compose.runtime.Composable
import com.typingarena.data.model.Difficulty
import com.typingarena.ui.viewmodel.TypingTestUiState

@Composable
fun PracticeScreen(
    uiState: TypingTestUiState,
    onInputTyped: (String) -> Unit,
    onResetTest: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TypingTestScreen(
        uiState = uiState.copy(isPracticeMode = true),
        onInputTyped = onInputTyped,
        onStartTest = { _, _ -> },
        onResetTest = onResetTest,
        onNavigateToResults = { },
        onNavigateBack = onNavigateBack
    )
}
