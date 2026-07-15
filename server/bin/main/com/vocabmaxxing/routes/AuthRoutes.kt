package com.vocabmaxxing.routes

import com.vocabmaxxing.database.Users
import com.vocabmaxxing.models.ErrorResponse
import com.vocabmaxxing.models.SyncResponse
import com.vocabmaxxing.plugins.FirebaseAdmin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Route.authRoutes() {

    // Called once after Firebase sign-in/register to create the user record in our DB.
    post("/api/auth/sync") {
        val authHeader = call.request.headers["Authorization"]
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing or invalid Authorization header."))
            return@post
        }

        val token = authHeader.removePrefix("Bearer ")

        val firebaseToken = try {
            FirebaseAdmin.verifyIdToken(token)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid Firebase token."))
            return@post
        }

        val uid = firebaseToken.uid
        val email = firebaseToken.email ?: ""

        transaction {
            val exists = Users.selectAll().where { Users.id eq uid }.count() > 0
            if (!exists) {
                Users.insert {
                    it[id] = uid
                    it[Users.email] = email
                    it[createdAt] = LocalDateTime.now()
                }
            }
        }

        call.respond(HttpStatusCode.OK, SyncResponse(userId = uid, email = email))
    }
}
