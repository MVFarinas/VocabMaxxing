package com.vocabmaxxing

import com.vocabmaxxing.services.ScoringEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScoringEngineTest {

    @Test
    fun `checkWordPresence detects exact match`() {
        assertTrue(ScoringEngine.checkWordPresence("The ubiquitous use of phones is alarming.", "ubiquitous"))
    }

    @Test
    fun `checkWordPresence detects morphological variant`() {
        assertTrue(ScoringEngine.checkWordPresence("She corroborated the findings.", "corroborate"))
    }

    @Test
    fun `checkWordPresence returns false when absent`() {
        assertFalse(ScoringEngine.checkWordPresence("The cat sat on the mat.", "ubiquitous"))
    }

    @Test
    fun `checkWordPresence handles multi-word targets`() {
        assertTrue(ScoringEngine.checkWordPresence("Due diligence revealed discrepancies.", "due diligence"))
    }

    @Test
    fun `scoreSentenceLength short sentence scores low`() {
        val score = ScoringEngine.scoreSentenceLength("Word here now.")
        assertTrue(score <= 6)
    }

    @Test
    fun `scoreSentenceLength medium sentence scores well`() {
        val score = ScoringEngine.scoreSentenceLength(
            "The researcher carefully examined the empirical evidence gathered from multiple independent sources."
        )
        assertTrue(score >= 16)
    }

    @Test
    fun `scoreStructuralComplexity simple sentence low`() {
        val score = ScoringEngine.scoreStructuralComplexity("The cat sat.")
        assertTrue(score <= 10)
    }

    @Test
    fun `scoreStructuralComplexity complex sentence high`() {
        val score = ScoringEngine.scoreStructuralComplexity(
            "Although the evidence was compelling, the committee, which had been skeptical, decided to request additional data."
        )
        assertTrue(score >= 10)
    }

    @Test
    fun `scoreGrammar proper sentence scores high`() {
        val score = ScoringEngine.scoreGrammar("The committee reached a unanimous decision.")
        assertTrue(score >= 16)
    }

    @Test
    fun `scoreGrammar missing capitalization penalized`() {
        val score = ScoringEngine.scoreGrammar("the committee reached a decision.")
        assertTrue(score < 20)
    }

    @Test
    fun `evaluate returns zero when word absent`() {
        val result = ScoringEngine.evaluate("The cat sat on the mat.", "ubiquitous")
        assertFalse(result.wordPresent)
        assertEquals(0, result.algorithmicTotal)
    }

    @Test
    fun `evaluate returns positive scores for valid sentence`() {
        val result = ScoringEngine.evaluate(
            "The ubiquitous presence of surveillance cameras raises significant privacy concerns.",
            "ubiquitous"
        )
        assertTrue(result.wordPresent)
        assertTrue(result.algorithmicTotal > 0)
        assertTrue(result.algorithmicTotal <= 60)
    }

    @Test
    fun `AI JSON response validation`() {
        val raw = """{"semantic_score": 32, "idiomatic_feedback": "Good usage.", "improvement_suggestion": "Try adding context."}"""
        val parsed = kotlinx.serialization.json.Json.decodeFromString<com.vocabmaxxing.services.SemanticResult>(raw)
        assertTrue(parsed.semantic_score in 0..40)
        assertTrue(parsed.idiomatic_feedback.isNotBlank())
        assertTrue(parsed.improvement_suggestion.isNotBlank())
    }

    @Test
    fun `AI JSON handles markdown fences`() {
        val raw = "```json\n{\"semantic_score\": 28, \"idiomatic_feedback\": \"Solid.\", \"improvement_suggestion\": \"More context.\"}\n```"
        val cleaned = raw.replace(Regex("```json\\n?|\\n?```"), "").trim()
        val parsed = kotlinx.serialization.json.Json.decodeFromString<com.vocabmaxxing.services.SemanticResult>(cleaned)
        assertEquals(28, parsed.semantic_score)
    }
}
