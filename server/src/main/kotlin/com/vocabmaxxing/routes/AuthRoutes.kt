package com.vocabmaxxing.routes

import com.vocabmaxxing.database.Users
import com.vocabmaxxing.models.*
import com.vocabmaxxing.plugins.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

fun Route.authRoutes() {

    post("/api/auth/register") {
        val request = call.receive<RegisterRequest>()

        if (request.email.isBlank() || !request.email.contains("@")) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid email address."))
            return@post
        }
        if (request.password.length < 8) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Password must be at least 8 characters."))
            return@post
        }

        val existing = transaction {
            Users.selectAll().where { Users.email eq request.email }.firstOrNull()
        }

        if (existing != null) {
            call.respond(HttpStatusCode.Conflict, ErrorResponse("An account with this email already exists."))
            return@post
        }

        val userId = UUID.randomUUID().toString()
        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt(12))

        transaction {
            Users.insert {
                it[id] = userId
                it[email] = request.email
                it[Users.passwordHash] = passwordHash
            }
        }

        val token = JwtConfig.generateToken(userId, request.email)
        call.respond(HttpStatusCode.Created, AuthResponse(token, userId, request.email))
    }

    post("/api/auth/login") {
        val request = call.receive<LoginRequest>()

        val user = transaction {
            Users.selectAll().where { Users.email eq request.email }.firstOrNull()
        }

        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("No account found with this email."))
            return@post
        }

        val validPassword = BCrypt.checkpw(request.password, user[Users.passwordHash])
        if (!validPassword) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid password."))
            return@post
        }

        val token = JwtConfig.generateToken(user[Users.id], user[Users.email])
        call.respond(HttpStatusCode.OK, AuthResponse(token, user[Users.id], user[Users.email]))
    }
}
