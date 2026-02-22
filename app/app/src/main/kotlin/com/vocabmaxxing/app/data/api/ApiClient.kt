package com.vocabmaxxing.app.data.api

import com.vocabmaxxing.app.BuildConfig
import com.vocabmaxxing.app.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient {

    private val baseUrl = BuildConfig.API_BASE_URL

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    // ─── Auth ────────────────────────────────────────────────────

    suspend fun register(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(email, password))
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val error = response.body<ErrorResponse>()
                Result.failure(Exception(error.error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val error = response.body<ErrorResponse>()
                Result.failure(Exception(error.error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Words ───────────────────────────────────────────────────

    suspend fun getDailyWords(token: String): Result<DailyWordsResponse> {
        return try {
            val response = client.get("$baseUrl/api/words/daily") {
                header("Authorization", "Bearer $token")
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to fetch daily words"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Attempts ────────────────────────────────────────────────

    suspend fun submitSentence(
        token: String,
        wordId: String,
        sentence: String
    ): Result<EvaluationResponse> {
        return try {
            val response = client.post("$baseUrl/api/attempts/evaluate") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(SubmitSentenceRequest(wordId, sentence))
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody = response.bodyAsText()
                val error = try {
                    json.decodeFromString<ErrorResponse>(errorBody)
                } catch (_: Exception) {
                    ErrorResponse("Evaluation failed.")
                }
                Result.failure(Exception(error.error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Dashboard ───────────────────────────────────────────────

    suspend fun getDashboard(token: String): Result<DashboardResponse> {
        return try {
            val response = client.get("$baseUrl/api/dashboard") {
                header("Authorization", "Bearer $token")
            }
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to load dashboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
