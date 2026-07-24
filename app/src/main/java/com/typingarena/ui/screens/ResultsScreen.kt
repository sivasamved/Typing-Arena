package com.typingarena.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.typingarena.data.model.PerformanceRating
import com.typingarena.ui.theme.EmeraldSuccess
import com.typingarena.ui.theme.IndigoPrimary
import com.typingarena.ui.theme.RoseError
import com.typingarena.ui.theme.TealSecondary
import com.typingarena.ui.viewmodel.TypingTestUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    uiState: TypingTestUiState,
    onRetry: () -> Unit,
    onReturnHome: () -> Unit
) {
    val context = LocalContext.current
    val rating = PerformanceRating.fromWpm(uiState.currentWpm)

    val ratingColor = when (rating) {
        PerformanceRating.BEGINNER -> Color(0xFF94A3B8)
        PerformanceRating.INTERMEDIATE -> TealSecondary
        PerformanceRating.ADVANCED -> IndigoPrimary
        PerformanceRating.EXPERT -> Color(0xFFF59E0B)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Test Results", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Rating Badge
            Surface(
                shape = CircleShape,
                color = ratingColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = rating.displayName().uppercase(),
                    color = ratingColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // Large Hero WPM Display
            Text(
                text = "${uiState.currentWpm}",
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "WORDS PER MINUTE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2x2 Breakdown Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultDetailCard(
                    title = "Accuracy",
                    value = "${uiState.accuracy.toInt()}%",
                    modifier = Modifier.weight(1f),
                    color = EmeraldSuccess
                )
                ResultDetailCard(
                    title = "Characters Typed",
                    value = "${uiState.userInput.length}",
                    modifier = Modifier.weight(1f),
                    color = TealSecondary
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultDetailCard(
                    title = "Correct Words",
                    value = "${uiState.correctWords}",
                    modifier = Modifier.weight(1f),
                    color = EmeraldSuccess
                )
                ResultDetailCard(
                    title = "Mistakes",
                    value = "${uiState.totalMistakes}",
                    modifier = Modifier.weight(1f),
                    color = RoseError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "RETRY TEST", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "I scored ${uiState.currentWpm} WPM with ${uiState.accuracy.toInt()}% accuracy in Typing Arena! ⚡")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Results"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share")
                }

                OutlinedButton(
                    onClick = onReturnHome,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Home")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ResultDetailCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
