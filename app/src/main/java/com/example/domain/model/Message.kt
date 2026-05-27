package com.example.domain.model

data class Message(
    val id: String,
    val conversationId: String,
    val text: String,
    val role: Role,
    val timestamp: Long,
    val imageUri: String? = null,
    val audioUri: String? = null
)

enum class Role {
    USER, ASSISTANT, SYSTEM
}
