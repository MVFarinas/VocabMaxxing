package com.vocabmaxxing

import com.vocabmaxxing.database.DatabaseFactory
import com.vocabmaxxing.database.WordSeeder
import com.vocabmaxxing.plugins.JwtConfig
import com.vocabmaxxing.routes.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun Application.module() {

    /* ---------------- DATABASE ---------------- */
    DatabaseFactory.init(environment)
    WordSeeder.seed()

    /* ---------------- JWT ---------------- */
    JwtConfig.init(environment)

    /* ---------------- CONTENT NEGOTIATION ---------------- */
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    /* ---------------- CORS ---------------- */
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    /* ---------------- LOGGING ---------------- */
    install(CallLogging) {
        level = Level.INFO
    }

    /* ---------------- ERROR HANDLING ---------------- */
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error")
            )
        }
    }

    /* ---------------- AUTH ---------------- */
    install(Authentication) {
        JwtConfig.configureAuth(this)
    }

    /* ---------------- OPENAI KEY ---------------- */
    val openAiApiKey =
        environment.config.propertyOrNull("openai.apiKey")?.getString() ?: ""

    /* ---------------- ROUTES ---------------- */
    routing {

        // Health check
        get("/health") {
            val dbStatus = if (DatabaseFactory.isConnected()) "connected" else "disconnected"
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "status" to "Backend running",
                    "database" to dbStatus
                )
            )
        }

        get("/db-check") {
            if (DatabaseFactory.isConnected()) {
                call.respond(HttpStatusCode.OK, mapOf("status" to "Database connection successful"))
            } else {
                call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "Database connection failed"))
            }
        }

        authRoutes()
        wordRoutes()
        attemptRoutes(openAiApiKey)
        dashboardRoutes()
        aiRoutes(openAiApiKey)
    }
}