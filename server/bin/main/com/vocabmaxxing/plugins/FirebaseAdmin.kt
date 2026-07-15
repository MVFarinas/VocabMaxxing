package com.vocabmaxxing.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Base64

data class FirebasePrincipal(val uid: String, val email: String) : Principal

object FirebaseAdmin {

    fun init(environment: ApplicationEnvironment) {
        val credentials = resolveCredentials(environment)

        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
        environment.log.info("Firebase Admin initialized.")
    }

    private fun resolveCredentials(environment: ApplicationEnvironment): GoogleCredentials {
        // 1. Env var: base64-encoded service account JSON (used in Railway)
        val encoded = System.getenv("FIREBASE_SERVICE_ACCOUNT")
        if (!encoded.isNullOrBlank()) {
            val json = Base64.getDecoder().decode(encoded)
            return GoogleCredentials.fromStream(ByteArrayInputStream(json))
        }

        // 2. Local dev: service-account.json file next to the jar / in working directory
        val localFile = File("service-account.json")
        if (localFile.exists()) {
            return GoogleCredentials.fromStream(localFile.inputStream())
        }

        error(
            "Firebase credentials not found. " +
            "Set FIREBASE_SERVICE_ACCOUNT env var (base64-encoded JSON) " +
            "or place service-account.json in the working directory."
        )
    }

    suspend fun verifyIdToken(token: String): FirebaseToken = withContext(Dispatchers.IO) {
        FirebaseAuth.getInstance().verifyIdToken(token)
    }
}

fun ApplicationCall.userId(): String = principal<FirebasePrincipal>()!!.uid
