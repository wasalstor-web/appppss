package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Message
import com.example.domain.model.Role

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val text: String,
    val role: String,
    val timestamp: Long,
    val imageUri: String? = null,
    val audioUri: String? = null
)

fun MessageEntity.toDomain() = Message(
    id = id,
    conversationId = conversationId,
    text = text,
    role = Role.valueOf(role),
    timestamp = timestamp,
    imageUri = imageUri,
    audioUri = audioUri
)

fun Message.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    text = text,
    role = role.name,
    timestamp = timestamp,
    imageUri = imageUri,
    audioUri = audioUri
)
