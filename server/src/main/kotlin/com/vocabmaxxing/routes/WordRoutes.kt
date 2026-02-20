package com.vocabmaxxing.routes

import com.vocabmaxxing.database.Attempts
import com.vocabmaxxing.database.Words
import com.vocabmaxxing.models.DailyWordsResponse
import com.vocabmaxxing.models.WordDto
import com.vocabmaxxing.plugins.userId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun Route.wordRoutes() {

    authenticate("auth-jwt") {

        get("/api/words/daily") {
            val userId = call.userId()

            val todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            val todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)

            val result = transaction {
                // Get today's attempts count
                val todayAttempts = Attempts.selectAll().where {
                    (Attempts.userId eq userId) and
                    (Attempts.createdAt greaterEq todayStart) and
                    (Attempts.createdAt lessEq todayEnd)
                }.count()

                // Get all previously attempted word IDs
                val attemptedWordIds = Attempts.selectAll()
                    .where { Attempts.userId eq userId }
                    .map { it[Attempts.wordId] }
                    .distinct()

                // Get one word per tier, excluding previously attempted
                val tiers = listOf("Academic", "Elite", "Professional")
                val dailyWords = mutableListOf<WordDto>()

                val dateSeed = LocalDate.now().toString()
                    .toCharArray().sumOf { it.code }

                for ((index, tier) in tiers.withIndex()) {
                    val available = Words.selectAll()
                        .where { (Words.tier eq tier) }
                        .filter { it[Words.id] !in attemptedWordIds }
                        .sortedBy { it[Words.rarityScore] }

                    val wordRow = if (available.isNotEmpty()) {
                        val tierOffset = when (index) {
                            0 -> 0; 1 -> 7; else -> 13
                        }
                        val idx = (dateSeed + tierOffset) % available.size
                        available[idx]
                    } else {
                        // Fallback: all words attempted, pick any from tier
                        Words.selectAll()
                            .where { Words.tier eq tier }
                            .firstOrNull()
                    }

                    wordRow?.let {
                        dailyWords.add(
                            WordDto(
                                id = it[Words.id],
                                word = it[Words.word],
                                definition = it[Words.definition],
                                exampleSentence = it[Words.exampleSentence],
                                tier = it[Words.tier],
                                rarityScore = it[Words.rarityScore]
                            )
                        )
                    }
                }

                DailyWordsResponse(
                    words = dailyWords,
                    completed = todayAttempts > 0,
                    attemptsToday = todayAttempts.toInt()
                )
            }

            call.respond(HttpStatusCode.OK, result)
        }
    }
}
