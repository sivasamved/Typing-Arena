package com.typingarena.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.typingarena.data.model.TypingResult
import com.typingarena.ui.theme.EmeraldSuccess
import com.typingarena.ui.theme.IndigoPrimary
import com.typingarena.ui.theme.TealSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    highestWpm: Int,
    averageWpm: Float,
    averageAccuracy: Float,
    totalTests: Int,
    totalTimeSeconds: Long,
    resultsHistory: List<TypingResult>,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Statistics & History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
            Spacer(modifier = Modifier.height(4.dp))

            // Summary Stats Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Highest WPM", value = "$highestWpm", modifier = Modifier.weight(1f), color = IndigoPrimary)
                StatCard(title = "Average WPM", value = "${averageWpm.toInt()}", modifier = Modifier.weight(1f), color = TealSecondary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Avg Accuracy", value = "${averageAccuracy.toInt()}%", modifier = Modifier.weight(1f), color = EmeraldSuccess)
                StatCard(title = "Tests Completed", value = "$totalTests", modifier = Modifier.weight(1f), color = Color(0xFF8B5CF6))
            }

            // Custom Compose WPM Trend Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "WPM Performance History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (resultsHistory.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No tests completed yet!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        val maxWpmInList = (resultsHistory.maxOfOrNull { it.wpm } ?: 100).toFloat()
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            val barWidth = (width / (resultsHistory.size.coerceAtMost(15) * 2f))
                            val spacing = barWidth

                            resultsHistory.take(15).reversed().forEachIndexed { index, item ->
                                val barHeight = (item.wpm / maxWpmInList) * height
                                val x = index * (barWidth + spacing) + spacing
                                val y = height - barHeight

                                drawRect(
                                    color = IndigoPrimary,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
