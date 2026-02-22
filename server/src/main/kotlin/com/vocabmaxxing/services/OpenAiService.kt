package com.vocabmaxxing.services

import com.vocabmaxxing.models.SentenceEvaluationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class OpenAiService(private val apiKey: String) {

    private val client = HttpClient(CIO)

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

        val requestBody = """
        {
          "model": "gpt-4-turbo-min",
          "messages": [
            {"role": "system", "content": "You are a strict English teacher."},
            {"role": "user", "content": ${Json.encodeToString(String.serializer(), prompt)}}
          ]
        }
        """.trimIndent()

        val response: HttpResponse = client.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(requestBody)
        }

        val rawBody = response.bodyAsText()

        // Extract only JSON part from OpenAI response
        val content = Regex("\"content\":\"(.*?)\"", RegexOption.DOT_MATCHES_ALL)
            .find(rawBody)
            ?.groupValues
            ?.get(1)
            ?.replace("\\n", "")
            ?.replace("\\\"", "\"")
            ?: throw Exception("Invalid AI response")

        return Json.decodeFromString(content)
    }
}