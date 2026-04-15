package com.vocabmaxxing.routes

import com.vocabmaxxing.database.Attempts
import com.vocabmaxxing.database.Users
import com.vocabmaxxing.database.Words
import com.vocabmaxxing.models.*
import com.vocabmaxxing.plugins.userId
import com.vocabmaxxing.services.AiScoringService
import com.vocabmaxxing.services.ScoringEngine
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.math.roundToInt

fun Route.attemptRoutes(openAiApiKey: String) {

    authenticate("auth-jwt") {

        post("/api/attempts/evaluate") {
            val userId = call.userId()
            val request = call.receive<SubmitSentenceRequest>()

            if (request.sentence.length < 10) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Sentence must be at least 10 characters."))
                return@post
            }
            if (request.sentence.length > 500) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Sentence must be at most 500 characters."))
                return@post
            }

            // Get the target word
            val word = transaction {
                Words.selectAll().where { Words.id eq request.wordId }.firstOrNull()
            }

            if (word == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Word not found."))
                return@post
            }

            val targetWord = word[Words.word]
            val definition = word[Words.definition]

            // Step 1: Algorithmic evaluation
            val algo = ScoringEngine.evaluate(request.sentence, targetWord)

            if (!algo.wordPresent) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("The word \"$targetWord\" (or a recognizable form) was not found in your sentence.")
                )
                return@post
            }

            // Step 2: AI semantic evaluation
            val semanticResult = AiScoringService.evaluate(
                targetWord, definition, request.sentence, openAiApiKey
            )
            val aiAvailable = semanticResult != null
            val semanticScore = semanticResult?.semantic_score ?: 0
            val totalScore = algo.algorithmicTotal + semanticScore

            val feedbackText = if (semanticResult != null) {
                "${semanticResult.idiomatic_feedback}\n\n${semanticResult.improvement_suggestion}"
            } else {
                "Semantic analysis unavailable. Partial evaluation shown."
            }

            // Step 3: Store attempt
            val attemptId = UUID.randomUUID().toString()
            transaction {
                Attempts.insert {
                    it[id] = attemptId
                    it[Attempts.userId] = userId
                    it[wordId] = request.wordId
                    it[sentence] = request.sentence
                    it[Attempts.semanticScore] = semanticScore
                    it[structuralScore] = algo.structuralComplexity
                    it[vocabScore] = algo.vocabularyDiversity
                    it[grammarScore] = algo.grammar
                    it[Attempts.totalScore] = totalScore
                    it[Attempts.feedbackText] = feedbackText
                    it[createdAt] = LocalDateTime.now()
                }
            }

            // Step 4: Update user stats
            val statsTriple = transaction {
                // RPI: rolling avg of last 20 attempts
                val recentScores = Attempts.selectAll()
                    .where { Attempts.userId eq userId }
                    .orderBy(Attempts.createdAt, SortOrder.DESC)
                    .limit(20)
                    .map { it[Attempts.totalScore] }

                val rpi = if (recentScores.isNotEmpty()) {
                    recentScores.average()
                } else 0.0

                val xp = (totalScore / 5.0).roundToInt()

                // Streak calculation
                val user = Users.selectAll().where { Users.id eq userId }.firstOrNull()
                    ?: return@transaction null
                val lastActive = user[Users.lastActiveAt]

                val streak = when {
                    lastActive == null -> 1
                    lastActive.toLocalDate() == LocalDate.now().minusDays(1) -> user[Users.streak] + 1
                    lastActive.toLocalDate() == LocalDate.now() -> user[Users.streak]
                    else -> 1
                }

                // Update user
                Users.update({ Users.id eq userId }) {
                    it[Users.rpi] = (rpi * 10).roundToInt() / 10.0
                    it[Users.xp] = user[Users.xp] + xp
                    it[Users.streak] = streak
                    it[lastActiveAt] = LocalDateTime.now()
                }

                Triple((rpi * 10).roundToInt() / 10.0, xp, streak)
            }

            if (statsTriple == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found."))
                return@post
            }
            val (newRpi, xpGain, newStreak) = statsTriple

            val response = EvaluationResponse(
                attemptId = attemptId,
                scores = ScoreBreakdown(
                    semanticScore = semanticScore,
                    structuralScore = algo.structuralComplexity,
                    vocabScore = algo.vocabularyDiversity,
                    grammarScore = algo.grammar,
                    totalScore = totalScore
                ),
                feedback = semanticResult?.let {
                    AiFeedback(
                        idiomaticFeedback = it.idiomatic_feedback,
                        improvementSuggestion = it.improvement_suggestion,
                        contextScore = it.context_score,
                        grammarScore = it.grammar_score,
                        complexityScore = it.complexity_score
                    )
                },
                feedbackText = feedbackText,
                aiAvailable = aiAvailable,
                xpGain = xpGain,
                newRpi = newRpi,
                streak = newStreak
            )

            call.respond(HttpStatusCode.OK, response)
        }
    }
}
