package com.vocabmaxxing

import com.vocabmaxxing.database.DatabaseFactory
import com.vocabmaxxing.database.WordSeeder
import com.vocabmaxxing.plugins.JwtConfig
import com.vocabmaxxing.routes.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Database
    DatabaseFactory.init(environment)
    WordSeeder.seed()

    // JWT
    JwtConfig.init(environment)

    // Plugins
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    install(CallLogging)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                com.vocabmaxxing.models.ErrorResponse("Internal server error.")
            )
        }
    }

    install(Authentication) {
        JwtConfig.configureAuth(this)
    }

    // OpenAI key
    val openAiApiKey = environment.config.propertyOrNull("openai.apiKey")?.getString() ?: ""

    // Routes
    routing {
        // Health check
        get("/api/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "ok", "service" to "vocabmaxxing"))
        }

        authRoutes()
        wordRoutes()
        attemptRoutes(openAiApiKey)
        dashboardRoutes()
    }
}
