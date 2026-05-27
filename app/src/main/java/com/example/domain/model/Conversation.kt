package com.example.domain.model

data class Conversation(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false
)
