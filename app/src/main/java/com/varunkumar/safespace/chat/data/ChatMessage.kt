package com.varunkumar.safespace.chat.data

data class ChatMessage (
    val data: String,
    val isBot: Boolean = false,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
