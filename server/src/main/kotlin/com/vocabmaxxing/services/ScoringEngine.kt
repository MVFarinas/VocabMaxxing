package com.vocabmaxxing.services

import kotlin.math.min
import kotlin.math.max
import kotlin.math.roundToInt

data class AlgorithmicResult(
    val wordPresent: Boolean,
    val sentenceLength: Int,
    val structuralComplexity: Int,
    val vocabularyDiversity: Int,
    val grammar: Int,
    val algorithmicTotal: Int
)

object ScoringEngine {

    private val STOP_WORDS = setOf(
        "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your",
        "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she",
        "her", "hers", "herself", "it", "its", "itself", "they", "them", "their",
        "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
        "these", "those", "am", "is", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an",
        "the", "and", "but", "if", "or", "because", "as", "until", "while", "of",
        "at", "by", "for", "with", "about", "against", "between", "through",
        "during", "before", "after", "above", "below", "to", "from", "up", "down",
        "in", "out", "on", "off", "over", "under", "again", "further", "then",
        "once", "here", "there", "when", "where", "why", "how", "all", "both",
        "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not",
        "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will",
        "just", "don", "should", "now"
    )

    private val SUBORDINATORS = setOf(
        "although", "because", "since", "unless", "whereas", "while", "when",
        "whenever", "where", "wherever", "if", "though", "even", "after", "before",
        "until", "as", "that", "which", "who", "whom", "whose"
    )

    private val CONJUNCTIONS = setOf("and", "but", "or", "nor", "for", "yet", "so")

    /**
     * Basic stemming: strips common suffixes.
     */
    private fun basicStem(word: String): String {
        val w = word.lowercase()
        if (w.length <= 4) return w
        val suffixes = listOf("ing", "tion", "sion", "ment", "ness", "able", "ible",
            "ous", "ive", "ful", "less", "ity", "ies", "ed", "ly", "er", "est", "al", "ial", "ical")
        for (suffix in suffixes) {
            if (w.endsWith(suffix) && w.length - suffix.length >= 3) {
                return w.dropLast(suffix.length)
            }
        }
        return w
    }

    /**
     * Check if the target word (or a morphological variant) is present in the sentence.
     */
    fun checkWordPresence(sentence: String, targetWord: String): Boolean {
        val sentenceLower = sentence.lowercase()
        val targetLower = targetWord.lowercase()

        // Handle multi-word targets like "due diligence"
        if (targetLower.contains(" ")) {
            return sentenceLower.contains(targetLower)
        }

        val words = sentenceLower.replace(Regex("[^a-z\\s'-]"), "").split(Regex("\\s+"))
        val targetStem = basicStem(targetLower)

        return words.any { w ->
            w == targetLower || w.contains(targetLower) || basicStem(w) == targetStem
        }
    }

    /**
     * Sentence Length Score (0–20).
     */
    fun scoreSentenceLength(sentence: String): Int {
        val wordCount = sentence.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        return when {
            wordCount < 5 -> 2
            wordCount < 8 -> 6
            wordCount < 12 -> 10
            wordCount < 18 -> 16
            wordCount <= 35 -> 20
            wordCount <= 50 -> 16
            else -> 10
        }
    }

    /**
     * Structural Complexity Score (0–20).
     */
    fun scoreStructuralComplexity(sentence: String): Int {
        val words = sentence.lowercase().split(Regex("\\s+"))
        val commaCount = sentence.count { it == ',' }
        val semicolonCount = sentence.count { it == ';' }
        val colonCount = sentence.count { it == ':' }
        val dashCount = Regex("[—–-]{2,}|—").findAll(sentence).count()

        var subordinatorCount = 0
        var conjunctionCount = 0
        for (w in words) {
            val clean = w.replace(Regex("[^a-z]"), "")
            if (clean in SUBORDINATORS) subordinatorCount++
            if (clean in CONJUNCTIONS) conjunctionCount++
        }

        var score = 4.0 // base score
        score += min(subordinatorCount * 3.0, 6.0)
        score += min(conjunctionCount * 1.5, 3.0)
        score += min(commaCount * 1.5, 4.0)
        score += semicolonCount * 2.0
        score += colonCount * 1.0
        score += dashCount * 1.0

        return min(score.roundToInt(), 20)
    }

    /**
     * Vocabulary Diversity Score (0–20).
     */
    fun scoreVocabularyDiversity(sentence: String): Int {
        val words = sentence.lowercase().replace(Regex("[^a-z\\s'-]"), "")
            .split(Regex("\\s+")).filter { it.isNotBlank() }

        if (words.isEmpty()) return 0

        val contentWords = words.filter { it !in STOP_WORDS }
        val uniqueContentWords = contentWords.toSet()

        val diversityRatio = if (contentWords.isNotEmpty()) {
            uniqueContentWords.size.toDouble() / contentWords.size
        } else 0.0

        var score = 0.0
        score += min(uniqueContentWords.size * 2.0, 12.0)
        score += diversityRatio * 8.0

        return min(score.roundToInt(), 20)
    }

    /**
     * Basic Grammar Check Score (0–20).
     */
    fun scoreGrammar(sentence: String): Int {
        val trimmed = sentence.trim()
        var score = 20

        // Must start with uppercase
        if (trimmed.isNotEmpty() && !trimmed[0].isUpperCase()) {
            score -= 4
        }

        // Must end with punctuation
        if (trimmed.isNotEmpty() && trimmed.last() !in listOf('.', '!', '?')) {
            score -= 4
        }

        // Double spaces
        if (Regex("\\s{2,}").containsMatchIn(trimmed)) {
            score -= 2
        }

        // Repeated words
        val words = trimmed.lowercase().split(Regex("\\s+"))
        for (i in 0 until words.size - 1) {
            if (words[i] == words[i + 1] && words[i].length > 2) {
                score -= 3
                break
            }
        }

        // Too short
        if (words.size < 4) {
            score -= 6
        }

        // Missing space after punctuation
        if (Regex("[.!?,;:][A-Za-z]").containsMatchIn(trimmed)) {
            score -= 2
        }

        return max(score, 0)
    }

    /**
     * Full algorithmic evaluation.
     */
    fun evaluate(sentence: String, targetWord: String): AlgorithmicResult {
        val wordPresent = checkWordPresence(sentence, targetWord)

        if (!wordPresent) {
            return AlgorithmicResult(
                wordPresent = false,
                sentenceLength = 0,
                structuralComplexity = 0,
                vocabularyDiversity = 0,
                grammar = 0,
                algorithmicTotal = 0
            )
        }

        val sentenceLength = scoreSentenceLength(sentence)
        val structural = scoreStructuralComplexity(sentence)
        val vocab = scoreVocabularyDiversity(sentence)
        val grammar = scoreGrammar(sentence)

        val lengthFactor = sentenceLength / 20.0
        val adjustedStructural = (structural * (0.5 + lengthFactor * 0.5)).roundToInt()

        return AlgorithmicResult(
            wordPresent = true,
            sentenceLength = sentenceLength,
            structuralComplexity = adjustedStructural,
            vocabularyDiversity = vocab,
            grammar = grammar,
            algorithmicTotal = adjustedStructural + vocab + grammar
        )
    }
}
