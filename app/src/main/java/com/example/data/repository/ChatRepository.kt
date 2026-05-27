package com.example.data.repository

import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.toDomain
import com.example.data.local.entity.toEntity
import com.example.domain.model.Conversation
import com.example.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { list -> list.map { it.toDomain() } }
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun createOrUpdateConversation(id: String, title: String) {
        val existing = conversationDao.getConversationById(id)
        if (existing == null) {
            conversationDao.insertConversation(
                ConversationEntity(
                    id = id,
                    title = title,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    isFavorite = false
                )
            )
        } else {
            conversationDao.updateConversationTitle(id, title, System.currentTimeMillis())
        }
    }

    suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
        // Also update conversation timestamp
        val conv = conversationDao.getConversationById(message.conversationId)
        if (conv != null) {
            conversationDao.updateConversationTitle(conv.id, conv.title, System.currentTimeMillis())
        }
    }

    suspend fun clearHistory(conversationId: String) {
        messageDao.deleteMessagesForConversation(conversationId)
    }
}
