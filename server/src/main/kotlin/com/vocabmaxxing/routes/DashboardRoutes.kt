package com.vocabmaxxing.routes

import com.vocabmaxxing.database.Attempts
import com.vocabmaxxing.database.Users
import com.vocabmaxxing.models.DashboardResponse
import com.vocabmaxxing.models.ScoreEntry
import com.vocabmaxxing.plugins.userId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

fun Route.dashboardRoutes() {

    authenticate("auth-jwt") {

        get("/api/dashboard") {
            val userId = call.userId()

            val response = transaction {
                val user = Users.selectAll().where { Users.id eq userId }.firstOrNull()
                    ?: return@transaction null

                val totalAttempts = Attempts.selectAll()
                    .where { Attempts.userId eq userId }.count()

                // Last 10 scores for chart
                val recentScores = Attempts.selectAll()
                    .where { Attempts.userId eq userId }
                    .orderBy(Attempts.createdAt, SortOrder.DESC)
                    .limit(10)
                    .map {
                        ScoreEntry(
                            totalScore = it[Attempts.totalScore],
                            semanticScore = it[Attempts.semanticScore],
                            structuralScore = it[Attempts.structuralScore],
                            vocabScore = it[Attempts.vocabScore],
                            grammarScore = it[Attempts.grammarScore],
                            createdAt = it[Attempts.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )
                    }
                    .reversed() // chronological order

                // 7-day trend
                val sevenDaysAgo = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN)
                val fourteenDaysAgo = LocalDateTime.of(LocalDate.now().minusDays(14), LocalTime.MIN)

                val last7 = Attempts.selectAll()
                    .where { Attempts.userId eq userId }
                    .andWhere { Attempts.createdAt greaterEq sevenDaysAgo }
                    .map { it[Attempts.totalScore] to it[Attempts.semanticScore] to it[Attempts.structuralScore] }

                val prev7 = Attempts.selectAll()
                    .where { Attempts.userId eq userId }
                    .andWhere { Attempts.createdAt greaterEq fourteenDaysAgo }
                    .andWhere { Attempts.createdAt less sevenDaysAgo }
                    .map { it[Attempts.totalScore] }

                val last7Scores = Attempts.selectAll()
                    .where { Attempts.userId eq userId }
                    .andWhere { Attempts.createdAt greaterEq sevenDaysAgo }
                    .toList()

                val last7Avg = if (last7Scores.isNotEmpty()) {
                    last7Scores.map { it[Attempts.totalScore] }.average()
                } else 0.0

                val prev7Avg = if (prev7.isNotEmpty()) prev7.average() else 0.0
                val trend = last7Avg - prev7Avg

                val semanticAvg = if (last7Scores.isNotEmpty()) {
                    last7Scores.map { it[Attempts.semanticScore] }.average()
                } else 0.0

                val structuralAvg = if (last7Scores.isNotEmpty()) {
                    last7Scores.map { it[Attempts.structuralScore] }.average()
                } else 0.0

                DashboardResponse(
                    rpi = user[Users.rpi],
                    xp = user[Users.xp],
                    streak = user[Users.streak],
                    totalAttempts = totalAttempts,
                    trend = (trend * 10).roundToInt() / 10.0,
                    semanticAvg = (semanticAvg * 10).roundToInt() / 10.0,
                    structuralAvg = (structuralAvg * 10).roundToInt() / 10.0,
                    recentScores = recentScores
                )
            }

            if (response == null) {
                call.respond(HttpStatusCode.NotFound, com.vocabmaxxing.models.ErrorResponse("User not found."))
            } else {
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}
