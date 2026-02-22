package com.vocabmaxxing.app.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.data.model.DashboardResponse
import com.vocabmaxxing.app.ui.components.StatCard
import com.vocabmaxxing.app.ui.theme.*

@Composable
fun DashboardScreen(
    data: DashboardResponse?,
    isLoading: Boolean,
    onNavigateDaily: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface950)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Performance Dashboard", fontSize = 26.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("Your rhetorical precision metrics at a glance.",
                    fontSize = 13.sp, color = TextMuted)
            }
            TextButton(onClick = onLogout) {
                Text("Sign out", fontSize = 12.sp, color = TextMuted)
            }
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading || data == null) {
            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("Loading...", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }
        } else {
            // Primary stats: 2x2 grid
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    label = "RPI", value = "${data.rpi}", valueColor = Accent,
                    subtext = "${if (data.trend >= 0) "+" else ""}${data.trend} 7d",
                    subtextColor = if (data.trend >= 0) ScoreHigh else ScoreLow,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Attempts", value = "${data.totalAttempts}",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(label = "XP", value = "${data.xp}", modifier = Modifier.weight(1f))
                StatCard(label = "Streak", value = "${data.streak}d", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // Secondary metrics
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(
                    modifier = Modifier.weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface900)
                        .padding(16.dp)
                ) {
                    Text("SEMANTIC ACCURACY AVG", fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace, color = TextMuted, letterSpacing = 1.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${data.semanticAvg}", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("/40", fontSize = 12.sp, color = TextMuted)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface900)
                        .padding(16.dp)
                ) {
                    Text("STRUCTURAL COMPLEXITY AVG", fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace, color = TextMuted, letterSpacing = 1.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${data.structuralAvg}", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("/20", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Score chart
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface900)
                    .padding(16.dp)
            ) {
                Text("LAST 10 SCORES", fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                    color = TextMuted, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(16.dp))

                if (data.recentScores.isNotEmpty()) {
                    SimpleLineChart(
                        scores = data.recentScores.map { it.totalScore },
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                } else {
                    Box(
                        Modifier.fillMaxWidth().height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No data yet. Complete your first evaluation.",
                            fontSize = 13.sp, color = TextMuted)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onNavigateDaily,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Surface950),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Start Daily Trifecta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Simple Canvas Line Chart ────────────────────────────────────

@Composable
fun SimpleLineChart(
    scores: List<Int>,
    modifier: Modifier = Modifier
) {
    val accentColor = Accent
    val gridColor = Color.White.copy(alpha = 0.03f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxScore = 100f
        val padding = 8f

        // Grid lines
        for (i in 0..4) {
            val y = padding + (height - 2 * padding) * (1 - i / 4f)
            drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
        }

        if (scores.size < 2) return@Canvas

        val stepX = (width - 2 * padding) / (scores.size - 1).coerceAtLeast(1)

        // Draw line
        val path = Path()
        scores.forEachIndexed { index, score ->
            val x = padding + index * stepX
            val y = padding + (height - 2 * padding) * (1 - score / maxScore)
            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        drawPath(path, accentColor, style = Stroke(width = 2.5f))

        // Draw dots
        scores.forEachIndexed { index, score ->
            val x = padding + index * stepX
            val y = padding + (height - 2 * padding) * (1 - score / maxScore)
            drawCircle(accentColor, radius = 4f, center = Offset(x, y))
        }
    }
}
