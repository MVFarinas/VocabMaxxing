package com.vocabmaxxing.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.max
import kotlin.math.min

@Serializable
data class SemanticResult(
    val semantic_score: Int,
    val idiomatic_feedback: String,
    val improvement_suggestion: String
)

object AiScoringService {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    private val SYSTEM_PROMPT = """
You are VocabMaxxing's semantic evaluation engine. You assess how precisely and idiomatically a user employs a given vocabulary word in a sentence.

Your evaluation criteria:
1. SEMANTIC CORRECTNESS (0-20): Does the sentence use the word with the correct meaning? Does context reinforce the definition?
2. IDIOMATIC PRECISION (0-20): Is the word used naturally, as a skilled writer would use it? Or does it feel forced, awkward, or stilted?

Scoring guide:
- 35-40: Exceptional. The sentence demonstrates mastery. Usage is precise, natural, and contextually rich.
- 25-34: Strong. Correct usage with good context, minor awkwardness.
- 15-24: Adequate. The word is used correctly but the sentence lacks depth or natural flow.
- 5-14: Weak. Partially correct usage but significant issues with meaning or naturalness.
- 0-4: Incorrect. The word is misused or the sentence is incoherent.

Rules:
- Be analytical and encouraging. Never shame the user.
- Be specific: reference the user's actual sentence in your feedback.
- Avoid generic praise. Point to what specifically works or doesn't.
- Keep feedback to 2-3 sentences maximum.
- Your improvement suggestion must be concrete and actionable.

You MUST respond with ONLY a JSON object, no markdown, no backticks:
{"semantic_score": <number 0-40>, "idiomatic_feedback": "<string>", "improvement_suggestion": "<string>"}
    """.trimIndent()

    suspend fun evaluate(
        targetWord: String,
        definition: String,
        userSentence: String,
        apiKey: String
    ): SemanticResult? {
        if (apiKey.isBlank()) {
            println("OPENAI_API_KEY not configured, skipping semantic evaluation.")
            return null
        }

        val userPrompt = """
Evaluate this sentence submission:

TARGET WORD: "$targetWord"
DEFINITION: "$definition"
USER SENTENCE: "$userSentence"

Return STRICT JSON only.
        """.trimIndent()

        return try {
            val response = client.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(
                    mapOf(
                        "model" to "gpt-4o-mini",
                        "messages" to listOf(
                            mapOf("role" to "system", "content" to SYSTEM_PROMPT),
                            mapOf("role" to "user", "content" to userPrompt)
                        ),
                        "temperature" to 0.3,
                        "max_tokens" to 300
                    )
                )
            }

            val body = response.bodyAsText()
            // Parse the OpenAI response to extract the content
            val openAiResponse = json.decodeFromString<OpenAiChatResponse>(body)
            val content = openAiResponse.choices.firstOrNull()?.message?.content?.trim()
                ?: return null

            // Strip markdown fences if present
            val cleaned = content.replace(Regex("```json\\n?|\\n?```"), "").trim()
            val result = json.decodeFromString<SemanticResult>(cleaned)

            // Clamp score to valid range
            result.copy(semantic_score = max(0, min(40, result.semantic_score)))
        } catch (e: Exception) {
            println("AI evaluation failed: ${e.message}")
            null
        }
    }
}

@Serializable
data class OpenAiChatResponse(
    val choices: List<OpenAiChoice>
)

@Serializable
data class OpenAiChoice(
    val message: OpenAiMessage
)

@Serializable
data class OpenAiMessage(
    val content: String? = null
)
