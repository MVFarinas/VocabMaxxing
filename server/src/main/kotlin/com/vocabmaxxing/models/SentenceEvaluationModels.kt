package com.vocabmaxxing.models

import kotlinx.serialization.Serializable

@Serializable
data class SentenceEvaluationRequest(
    val word: String,
    val sentence: String
)

@Serializable
data class SentenceEvaluationResponse(
    val score: Int,
    val correctSpelling: Boolean,
    val usedCorrectly: Boolean,
    val feedback: String,
    val improvedSentence: String? = null
)