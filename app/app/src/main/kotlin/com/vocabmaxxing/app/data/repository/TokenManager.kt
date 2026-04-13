package com.vocabmaxxing.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class TokenManager {

    suspend fun getFreshToken(): String? = try {
        FirebaseAuth.getInstance().currentUser
            ?.getIdToken(true)
            ?.await()
            ?.token
    } catch (e: Exception) {
        null
    }

    fun isAuthenticated(): Boolean =
        FirebaseAuth.getInstance().currentUser != null

    fun getCurrentUserId(): String? =
        FirebaseAuth.getInstance().currentUser?.uid

    fun getCurrentEmail(): String? =
        FirebaseAuth.getInstance().currentUser?.email

    fun clearSession() {
        FirebaseAuth.getInstance().signOut()
    }
}
