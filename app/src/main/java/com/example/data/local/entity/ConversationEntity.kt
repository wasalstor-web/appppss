package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Conversation

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean
)

fun ConversationEntity.toDomain() = Conversation(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFavorite = isFavorite
)

fun Conversation.toEntity() = ConversationEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFavorite = isFavorite
)
