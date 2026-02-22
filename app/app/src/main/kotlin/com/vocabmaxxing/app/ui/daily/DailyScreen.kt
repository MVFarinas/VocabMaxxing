package com.vocabmaxxing.app.ui.daily

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.data.model.EvaluationResponse
import com.vocabmaxxing.app.data.model.WordDto
import com.vocabmaxxing.app.ui.components.ScoreBar
import com.vocabmaxxing.app.ui.components.StatCard
import com.vocabmaxxing.app.ui.components.WordCard
import com.vocabmaxxing.app.ui.theme.*

@Composable
fun DailyScreen(
    words: List<WordDto>,
    isLoading: Boolean,
    isSubmitting: Boolean,
    error: String?,
    result: EvaluationResponse?,
    onSubmit: (wordId: String, sentence: String) -> Unit,
    onReset: () -> Unit,
    onNavigateDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWordId by remember { mutableStateOf("") }
    var sentence by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface950)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text(
            text = "Daily Elite Trifecta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Select a word. Construct a precise sentence. Receive analytical feedback.",
            fontSize = 13.sp,
            color = TextMuted
        )
        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("Loading...", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }
        } else if (result != null) {
            // ─── Result View ─────────────────────────────────────
            val selectedWord = words.find { it.id == selectedWordId }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(label = "XP Earned", value = "+${result.xpGain}",
                    valueColor = Accent, modifier = Modifier.weight(1f))
                StatCard(label = "RPI", value = "${result.newRpi}",
                    modifier = Modifier.weight(1f))
                StatCard(label = "Streak", value = "${result.streak}d",
                    modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // User's sentence
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface900)
                    .padding(16.dp)
            ) {
                Text("YOUR SENTENCE", fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                    color = TextMuted, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(8.dp))
                Text("“$sentence”", fontSize = 14.sp, color = TextSecondary,
                    fontStyle = FontStyle.Italic, lineHeight = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text("Target: ${selectedWord?.word ?: ""}", fontSize = 12.sp, color = Accent)
            }

            Spacer(Modifier.height(16.dp))

            // Overall score
            Column(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PRECISION SCORE", fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                    color = TextMuted, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(8.dp))
                val scoreColor = when {
                    result.scores.totalScore >= 70 -> ScoreHigh
                    result.scores.totalScore >= 40 -> ScoreMid
                    else -> ScoreLow
                }
                Text("${result.scores.totalScore}", fontSize = 56.sp,
                    fontWeight = FontWeight.Bold, color = scoreColor)
                Text("/100", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
            }

            // Score breakdown
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface900)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("SCORE BREAKDOWN", fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                    color = TextMuted, letterSpacing = 1.5.sp)
                ScoreBar("Semantic Precision", result.scores.semanticScore, 40)
                ScoreBar("Structural Complexity", result.scores.structuralScore, 20)
                ScoreBar("Vocabulary Density", result.scores.vocabScore, 20)
                ScoreBar("Grammar Stability", result.scores.grammarScore, 20)
            }

            Spacer(Modifier.height(16.dp))

            // AI Feedback
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface900)
                    .padding(16.dp)
            ) {
                Text(
                    if (result.aiAvailable) "SEMANTIC ANALYSIS" else "EVALUATION NOTE",
                    fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                    color = TextMuted, letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(12.dp))

                if (!result.aiAvailable) {
                    Text("Semantic analysis unavailable. Partial evaluation shown.",
                        fontSize = 12.sp, color = ScoreMid.copy(alpha = 0.8f),
                        fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(8.dp))
                }

                if (result.feedback != null) {
                    Text(result.feedback.idiomaticFeedback,
                        fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
                    Spacer(Modifier.height(12.dp))
                    Divider(color = Color.White.copy(alpha = 0.03f))
                    Spacer(Modifier.height(12.dp))
                    Text("SUGGESTION", fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                        color = TextMuted, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(result.feedback.improvementSuggestion,
                        fontSize = 13.sp, color = TextMuted, lineHeight = 20.sp)
                } else {
                    Text(result.feedbackText, fontSize = 14.sp, color = TextMuted, lineHeight = 22.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onReset(); selectedWordId = ""; sentence = "" },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Surface950),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Try Another", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
                OutlinedButton(
                    onClick = onNavigateDashboard,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) { Text("Dashboard", fontSize = 14.sp) }
            }

        } else {
            // ─── Word Selection ──────────────────────────────────
            Text("SELECT A WORD", fontSize = 9.sp, fontFamily = FontFamily.Monospace,
                color = TextMuted, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(12.dp))

            words.forEach { word ->
                WordCard(
                    word = word.word,
                    definition = word.definition,
                    exampleSentence = word.exampleSentence,
                    tier = word.tier,
                    rarityScore = word.rarityScore,
                    selected = selectedWordId == word.id,
                    onClick = { selectedWordId = word.id }
                )
                Spacer(Modifier.height(10.dp))
            }

            if (words.isEmpty()) {
                Text("No words available.", fontSize = 13.sp, color = TextMuted)
            }

            // ─── Sentence Input ──────────────────────────────────
            val selectedWord = words.find { it.id == selectedWordId }
            if (selectedWord != null) {
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface900)
                        .padding(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("COMPOSE YOUR SENTENCE", fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace, color = TextMuted,
                                letterSpacing = 1.5.sp)
                            Spacer(Modifier.height(4.dp))
                            Row {
                                Text("Use ", fontSize = 13.sp, color = TextSecondary)
                                Text(selectedWord.word, fontSize = 13.sp, color = Accent,
                                    fontWeight = FontWeight.Medium)
                                Text(" precisely.", fontSize = 13.sp, color = TextSecondary)
                            }
                        }
                        Text(
                            "${sentence.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size} words",
                            fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextMuted
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sentence,
                        onValueChange = { if (it.length <= 500) sentence = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        placeholder = {
                            Text("Write a sentence using \"${selectedWord.word}\"...",
                                color = TextMuted.copy(alpha = 0.4f))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Accent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(error, fontSize = 13.sp, color = ScoreLow.copy(alpha = 0.9f))
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { onSubmit(selectedWordId, sentence) },
                        enabled = !isSubmitting && sentence.trim().length >= 10,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent, contentColor = Surface950
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (isSubmitting) "Evaluating..." else "Submit for Evaluation",
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
