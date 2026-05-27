package com.example.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ChatRepository
import com.example.domain.model.Message
import com.example.domain.model.Role
import com.example.domain.repository.AiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val conversationId: String,
    private val chatRepository: ChatRepository,
    private val aiService: AiService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            chatRepository.createOrUpdateConversation(conversationId, "محادثة جديدة")
            chatRepository.getMessagesForConversation(conversationId).collect { msgList ->
                _messages.value = msgList
            }
        }
    }

    fun sendMessage(text: String, imageUri: String? = null, audioUri: String? = null) {
        if (text.isBlank() && imageUri == null && audioUri == null) return
        val finalPrompt = if (text.isBlank()) {
            when {
                imageUri != null && audioUri != null -> "تحليل ملف صوتي وصورة مرفقة 📎"
                imageUri != null -> "تحليل صورة مرفقة 📷"
                audioUri != null -> "تحليل تسجيل صوتي 🎙️"
                else -> ""
            }
        } else text

        viewModelScope.launch {
            val userMsg = Message(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                text = finalPrompt,
                role = Role.USER,
                timestamp = System.currentTimeMillis(),
                imageUri = imageUri,
                audioUri = audioUri
            )
            chatRepository.insertMessage(userMsg)
            
            _isLoading.value = true
            
            val result = aiService.sendMessage(_messages.value, finalPrompt)
            
            result.onSuccess { reply ->
                val aiMsg = Message(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    text = reply,
                    role = Role.ASSISTANT,
                    timestamp = System.currentTimeMillis()
                )
                chatRepository.insertMessage(aiMsg)
                
                // Update conversation title based on first user message if needed
                if (_messages.value.size <= 2) {
                    val title = if (finalPrompt.length > 20) finalPrompt.take(20) + "..." else finalPrompt
                    chatRepository.createOrUpdateConversation(conversationId, title)
                }
            }.onFailure { error ->
                val errorMsg = Message(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    text = error.message ?: "حدث خطأ غير متوقع.",
                    role = Role.SYSTEM,
                    timestamp = System.currentTimeMillis()
                )
                chatRepository.insertMessage(errorMsg)
            }
            
            _isLoading.value = false
        }
    }
}
