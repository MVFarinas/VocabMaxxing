package com.vocabmaxxing.app.data.model

import kotlinx.serialization.Serializable

// ─── Auth ────────────────────────────────────────────────────────

@Serializable
data class SyncResponse(val userId: String, val email: String)

@Serializable
data class ErrorResponse(val error: String)

// ─── Words ───────────────────────────────────────────────────────

@Serializable
data class WordDto(
    val id: String,
    val word: String,
    val definition: String,
    val exampleSentence: String,
    val tier: String,
    val rarityScore: Int
)

@Serializable
data class DailyWordsResponse(
    val words: List<WordDto>,
    val completed: Boolean,
    val attemptsToday: Int
)

// ─── Attempts / Scoring ─────────────────────────────────────────

@Serializable
data class SubmitSentenceRequest(val wordId: String, val sentence: String)

@Serializable
data class ScoreBreakdown(
    val semanticScore: Int,
    val structuralScore: Int,
    val vocabScore: Int,
    val grammarScore: Int,
    val totalScore: Int
)

@Serializable
data class AiFeedback(
    val idiomaticFeedback: String,
    val improvementSuggestion: String
)

@Serializable
data class EvaluationResponse(
    val attemptId: String,
    val scores: ScoreBreakdown,
    val feedback: AiFeedback? = null,
    val feedbackText: String,
    val aiAvailable: Boolean,
    val xpGain: Int,
    val newRpi: Double,
    val streak: Int
)

// ─── Dashboard ───────────────────────────────────────────────────

@Serializable
data class DashboardResponse(
    val rpi: Double,
    val xp: Int,
    val streak: Int,
    val totalAttempts: Long,
    val trend: Double,
    val semanticAvg: Double,
    val structuralAvg: Double,
    val recentScores: List<ScoreEntry>
)

@Serializable
data class ScoreEntry(
    val totalScore: Int,
    val semanticScore: Int,
    val structuralScore: Int,
    val vocabScore: Int,
    val grammarScore: Int,
    val createdAt: String
)
