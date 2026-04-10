package com.vocabmaxxing.services

import com.vocabmaxxing.models.SentenceEvaluationResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OpenAiService(private val apiKey: String) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun evaluateSentence(word: String, sentence: String): SentenceEvaluationResponse {

        val prompt = """
You are a strict but fair English vocabulary teacher.

Evaluate the student's sentence using the target word.

Target word: "$word"
Student sentence: "$sentence"

Return ONLY valid JSON in this format:

{
  "score": 0-10,
  "correctSpelling": true/false,
  "usedCorrectly": true/false,
  "feedback": "Short explanation",
  "improvedSentence": "Better example sentence if needed or null"
}

Be strict. Do not include extra commentary.
""".trimIndent()

        val response: HttpResponse = client.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(
                mapOf(
                    "model" to "gpt-4o-mini",
                    "messages" to listOf(
                        mapOf("role" to "system", "content" to "You are a strict English teacher."),
                        mapOf("role" to "user", "content" to prompt)
                    )
                )
            )
        }

        val body = response.bodyAsText()
        val openAiResponse = json.decodeFromString<OpenAiLegacyResponse>(body)
        val content = openAiResponse.choices.firstOrNull()?.message?.content?.trim()
            ?: throw Exception("Invalid AI response: no content in response")

        val cleaned = content.replace(Regex("```json\\n?|\\n?```"), "").trim()
        return json.decodeFromString(cleaned)
    }
}

@Serializable
private data class OpenAiLegacyResponse(val choices: List<OpenAiLegacyChoice>)

@Serializable
private data class OpenAiLegacyChoice(val message: OpenAiLegacyMessage)

@Serializable
private data class OpenAiLegacyMessage(val content: String? = null)
