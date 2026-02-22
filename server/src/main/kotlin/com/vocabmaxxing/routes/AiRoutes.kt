package com.vocabmaxxing.routes

import com.vocabmaxxing.models.SentenceEvaluationRequest
import com.vocabmaxxing.services.OpenAiService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.aiRoutes(openAiApiKey: String) {

    val openAiService = OpenAiService(openAiApiKey)

    post("/ai/evaluate") {

        val request = call.receive<SentenceEvaluationRequest>()

        val evaluation = openAiService.evaluateSentence(
            request.word,
            request.sentence
        )

        call.respond(HttpStatusCode.OK, evaluation)
    }
}