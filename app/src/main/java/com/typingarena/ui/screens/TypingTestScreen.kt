package com.typingarena.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.typingarena.data.model.Difficulty
import com.typingarena.ui.theme.*
import com.typingarena.ui.viewmodel.TypingTestUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingTestScreen(
    uiState: TypingTestUiState,
    onInputTyped: (String) -> Unit,
    onStartTest: (Int, Difficulty) -> Unit,
    onResetTest: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished && !uiState.isPracticeMode) {
            onNavigateToResults()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (uiState.isPracticeMode) "Practice Mode" else "Typing Test", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onResetTest) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Duration Pills Selector (only in timed test mode)
            if (!uiState.isPracticeMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(15, 30, 60, 120).forEach { sec ->
                        FilterChip(
                            selected = uiState.durationSeconds == sec,
                            onClick = { onStartTest(sec, uiState.difficulty) },
                            label = { Text("${sec}s") },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            // Real-Time Stats Header Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMetricCard(
                    title = if (uiState.isPracticeMode) "STATUS" else "TIME",
                    value = if (uiState.isPracticeMode) "Unlimited" else "${uiState.timeRemainingSeconds}s",
                    modifier = Modifier.weight(1f),
                    color = IndigoPrimary
                )
                StatMetricCard(
                    title = "SPEED",
                    value = "${uiState.currentWpm} WPM",
                    modifier = Modifier.weight(1f),
                    color = TealSecondary
                )
                StatMetricCard(
                    title = "ACCURACY",
                    value = "${uiState.accuracy.toInt()}%",
                    modifier = Modifier.weight(1f),
                    color = EmeraldSuccess
                )
            }

            // Character Color Highlighting Display Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .minHeight(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    val annotatedString = buildAnnotatedString {
                        val target = uiState.targetText
                        val input = uiState.userInput

                        for (i in target.indices) {
                            when {
                                i < input.length -> {
                                    if (input[i] == target[i]) {
                                        withStyle(style = SpanStyle(color = EmeraldSuccess, fontWeight = FontWeight.Bold)) {
                                            append(target[i])
                                        }
                                    } else {
                                        withStyle(style = SpanStyle(color = RoseError, fontWeight = FontWeight.Bold)) {
                                            append(target[i])
                                        }
                                    }
                                }
                                i == input.length -> {
                                    // Current cursor character position
                                    withStyle(style = SpanStyle(color = ActiveCursorBlue, background = ActiveCursorBlue.copy(alpha = 0.25f), fontWeight = FontWeight.ExtraBold)) {
                                        append(target[i])
                                    }
                                }
                                else -> {
                                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))) {
                                        append(target[i])
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        lineHeight = 30.sp
                    )

                    // Hidden/Overlay TextField to intercept soft & hard key events
                    BasicTextField(
                        value = uiState.userInput,
                        onValueChange = onInputTyped,
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Transparent)
                    )
                }
            }

            Text(
                text = "Tap on the box above to start typing!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
