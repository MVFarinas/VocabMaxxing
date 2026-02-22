package com.vocabmaxxing.app

import android.app.Application
import com.vocabmaxxing.app.data.api.ApiClient
import com.vocabmaxxing.app.data.repository.TokenManager

class VocabMaxxingApp : Application() {

    lateinit var apiClient: ApiClient
        private set

    lateinit var tokenManager: TokenManager
        private set

    override fun onCreate() {
        super.onCreate()
        apiClient = ApiClient()
        tokenManager = TokenManager(this)
    }
}
