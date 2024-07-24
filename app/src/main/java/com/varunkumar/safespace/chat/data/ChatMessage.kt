package com.varunkumar.safespace.chat.data

data class ChatMessage (
    val data: String,
    val isBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
