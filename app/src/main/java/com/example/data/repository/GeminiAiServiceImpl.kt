package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.BuildConfig
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.Part
import com.example.data.remote.RetrofitClient
import com.example.domain.model.Message
import com.example.domain.model.Role
import com.example.domain.repository.AiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class GeminiAiServiceImpl(private val context: Context) : AiService {

    private fun uriToBase64(uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close() ?: return null
            
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun sendMessage(history: List<Message>, newMessageText: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val lastUserMsg = history.lastOrNull { it.role == Role.USER } ?: Message(
                id = "",
                conversationId = "",
                text = newMessageText,
                role = Role.USER,
                timestamp = System.currentTimeMillis()
            )
            val hasImage = lastUserMsg.imageUri != null || history.any { it.role == Role.USER && it.imageUri != null }
            val hasAudio = lastUserMsg.audioUri != null || history.any { it.role == Role.USER && it.audioUri != null }

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Mock smart and charming response simulating visual and voice comprehension
                kotlinx.coroutines.delay(1200)
                val reply = when {
                    hasImage && hasAudio -> "أهلاً بك في البوابة الذكية لمُبسَّط! لقد استمعتُ إلى تسجيلك الصوتي المرفق وشاهدتُ الصورة التي رفعتها بدقة عالية. بناءً على رؤيتك في التسجيل وتوجيهاتك البصرية في الصورة، سأبدأ فوراً في صياغة سيناريو إبداعي وتحديد معايير الللقطات والرندرة (Render Settings) لأتمتة أعمالك!"
                    hasImage -> "أهلاً بك يا مبدع! لقد استلمتُ الصورة التي قمت برفعها عبر واجهة مُبَسَّط للأتمتة بنجاح. قمت بتحليل تفاصيلها البصرية المميزة، ويسعدني استخدامها الآن لتصميم الكوادر السينمائية واللقطات المناسبة تماماً لمشروعك."
                    hasAudio -> "مرحباً بك! لقد استمعتُ بوضوح إلى تسجيلك الصوتي المرفق وسأباشر فوراً بتحويل النقاط التي ذكرتها صوتياً إلى خطة إنتاج وخطوات عمل ذكية ومتكاملة. كيف يمكنني إبهارك وتسهيل مهامك اليوم؟"
                    else -> "أهلاً بك في منصة مُبَسَّط الذكية! لقد تم تفعيل وضع المحاكاة بنجاح لضمان تشغيل المنصة بسلاسة. كيف يمكنني مساعدتك اليوم في تصميم فكرتك الإبداعية وصياغة سيناريوهات الفيديو والتحكم الكامل بمشروعك؟"
                }
                return@withContext Result.success(reply)
            }

            val contents = mutableListOf<Content>()
            var alreadyContainsNewMsg = false

            history.forEach { msg ->
                val partsList = mutableListOf<Part>()
                partsList.add(Part(text = msg.text))
                
                if (msg.imageUri != null) {
                    val base64 = uriToBase64(msg.imageUri)
                    if (base64 != null) {
                        partsList.add(Part(inlineData = com.example.data.remote.InlineData(mimeType = "image/jpeg", data = base64)))
                    }
                }
                
                contents.add(Content(
                    role = if (msg.role == Role.USER) "user" else "model",
                    parts = partsList
                ))

                if (msg.text == newMessageText && msg.role == Role.USER) {
                    alreadyContainsNewMsg = true
                }
            }

            if (!alreadyContainsNewMsg) {
                val currentParts = mutableListOf<Part>()
                currentParts.add(Part(text = newMessageText))
                val lastMsgWithImage = history.lastOrNull { it.role == Role.USER && it.imageUri != null }
                if (lastMsgWithImage?.imageUri != null) {
                    val base64 = uriToBase64(lastMsgWithImage.imageUri)
                    if (base64 != null) {
                        currentParts.add(Part(inlineData = com.example.data.remote.InlineData(mimeType = "image/jpeg", data = base64)))
                    }
                }
                contents.add(Content(role = "user", parts = currentParts))
            }

            val systemInstruction = Content(
                parts = listOf(Part(text = "أنت مساعد ذكي لمنصة تبسيط (Tabseet). أجب باللغة العربية واجعل إجاباتك موجزة وواضحة واحترافية وملهمة للأعمال الإبداعية."))
            )

            val request = GenerateContentRequest(
                systemInstruction = systemInstruction,
                contents = contents
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "عذراً، لم أتمكن من الإجابة حالياً."

            Result.success(replyText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
