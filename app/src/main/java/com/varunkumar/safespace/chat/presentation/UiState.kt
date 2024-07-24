package com.varunkumar.safespace.chat.presentation

sealed interface UiState {
    data object Initial : UiState
    data object Loading : UiState
    data class Success(val outputText: String, val timestamp: Long) : UiState
    data class Error(val errorMessage: String?) : UiState
}