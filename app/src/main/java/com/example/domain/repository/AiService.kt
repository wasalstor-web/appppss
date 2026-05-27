package com.example.domain.repository

import com.example.domain.model.Message

interface AiService {
    suspend fun sendMessage(
        history: List<Message>,
        newMessageText: String
    ): Result<String>
}
