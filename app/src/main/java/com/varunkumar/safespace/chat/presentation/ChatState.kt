package com.varunkumar.safespace.chat.presentation

import com.varunkumar.safespace.chat.data.ChatMessage
import com.varunkumar.safespace.utils.Result

data class ChatState(
    val result: Result<Boolean> = Result.Loading(),
    val message: String = "",
    val uiState: UiState = UiState.Initial,
    val speakText: ChatMessage? = null,
    val isSpeaking: Boolean = false,
    val isTyping: Boolean = false,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(data = "Hello! How can i help you?", isBot = true)
    )
)