package com.vocabmaxxing.app.ui.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaxxing.app.data.api.ApiClient
import com.vocabmaxxing.app.data.model.EvaluationResponse
import com.vocabmaxxing.app.data.model.WordDto
import com.vocabmaxxing.app.data.repository.TokenManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DailyUiState(
    val words: List<WordDto> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val result: EvaluationResponse? = null
)

class DailyViewModel(
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyUiState())
    val uiState: StateFlow<DailyUiState> = _uiState.asStateFlow()

    init {
        loadDailyWords()
    }

    fun loadDailyWords() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val token = tokenManager.token.first()
            if (token == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Not authenticated.")
                return@launch
            }

            apiClient.getDailyWords(token)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        words = response.words,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load words."
                    )
                }
        }
    }

    fun submitSentence(wordId: String, sentence: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)

            val token = tokenManager.token.first()
            if (token == null) {
                _uiState.value = _uiState.value.copy(isSubmitting = false, error = "Not authenticated.")
                return@launch
            }

            apiClient.submitSentence(token, wordId, sentence)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        result = response
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = e.message ?: "Evaluation failed."
                    )
                }
        }
    }

    fun reset() {
        _uiState.value = _uiState.value.copy(result = null, error = null)
    }
}
