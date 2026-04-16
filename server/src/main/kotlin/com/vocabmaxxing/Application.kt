package com.vocabmaxxing

import com.vocabmaxxing.database.DatabaseFactory
import com.vocabmaxxing.database.WordSeeder
import com.vocabmaxxing.plugins.FirebaseAdmin
import com.vocabmaxxing.plugins.FirebasePrincipal
import com.vocabmaxxing.routes.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.bearer
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

    /* ---------------- FIREBASE ---------------- */
    FirebaseAdmin.init(environment)

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
        bearer("auth-jwt") {
            authenticate { credential ->
                try {
                    val firebaseToken = FirebaseAdmin.verifyIdToken(credential.token)
                    FirebasePrincipal(uid = firebaseToken.uid, email = firebaseToken.email ?: "")
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    /* ---------------- AI PROVIDER CONFIG ---------------- */
    val aiApiKey = environment.config.propertyOrNull("ai.apiKey")?.getString() ?: ""
    val aiBaseUrl = environment.config.propertyOrNull("ai.baseUrl")?.getString()
        ?: "https://api.openai.com/v1/chat/completions"
    val aiModel = environment.config.propertyOrNull("ai.model")?.getString() ?: "gpt-4o-mini"

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
        attemptRoutes(aiApiKey, aiBaseUrl, aiModel)
        dashboardRoutes()
    }
}