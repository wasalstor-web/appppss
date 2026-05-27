package com.example.presentation.screens.tools

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.domain.repository.AiService
import com.example.ui.theme.BrandTiffany
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailScreen(
    toolId: String,
    navController: NavController,
    aiService: AiService
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // UI State for dynamic tools
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(toolId) {
        title = when (toolId) {
            "text_generator" -> "مساعد الإنشاء الذكي"
            "video_ai" -> "منصة التوليد المرئيAI"
            "automations" -> "محرر الأتمتة والتدفقات"
            "integrations" -> "مركز الربط والتكاملات"
            "call_center" -> "الكول سنتر والمساعد الصوتي"
            else -> "مساعد الذكاء الاصطناعي"
        }
        description = when (toolId) {
            "text_generator" -> "نموذج توليد نصوص ذكي مدعوم بـ Gemini لصناعة المحتوى والمقالات والردود الفورية"
            "video_ai" -> "إنشاء مقاطع فيديو بجودة سينمائية من خلال الأوصاف النصية والصوتية المتقدمة"
            "automations" -> "ربط العمليات تلقائياً وتفعيل المحفزات والوظائف لحسابك بدون برمجة"
            "integrations" -> "تكاملات مباشرة وسهلة مع أشهر المنصات العالمية مثل WhatsApp وSlack وZapier"
            "call_center" -> "إدارة كاملة لمركز الاتصال الذكي باستخدام تكنولوجيا تحويل الصوت إلى نصوص في الوقت الحقيقي"
            else -> ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandTiffany.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (toolId) {
                                "text_generator" -> Icons.Default.Create
                                "video_ai" -> Icons.Default.PlayArrow
                                "automations" -> Icons.Default.Settings
                                "integrations" -> Icons.Default.Share
                                "call_center" -> Icons.Default.Call
                                else -> Icons.Default.Info
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = BrandTiffany,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                modifier = Modifier.padding(top = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = BrandTiffany.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "إصدار احترافي Pro",
                                    fontSize = 11.sp,
                                    color = BrandTiffany,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    )
                }
            }

            // Specific content template
            when (toolId) {
                "text_generator" -> TextGeneratorWorkspace(aiService, scope, clipboardManager)
                "video_ai" -> VideoAiWorkspace(scope)
                "automations" -> AutomationsWorkspace(scope)
                "integrations" -> IntegrationsWorkspace(scope, clipboardManager)
                "call_center" -> CallCenterWorkspace(scope)
                else -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("الأداة المطلوبة غير متوفرة حالياً")
                    }
                }
            }
        }
    }
}

// 1. Text Generator Workspace Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextGeneratorWorkspace(
    aiService: AiService,
    scope: kotlinx.coroutines.CoroutineScope,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    var prompt by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("مهني احترافي") }
    var selectedModel by remember { mutableStateOf("Gemini 1.5 Pro (تبسيط)") }
    var temperature by remember { mutableStateOf(0.7f) }
    var isLoading by remember { mutableStateOf(false) }

    val tones = listOf("مهني احترافي", "إبداعي شيق", "تسويقي مقنع", "بسيط ومباشر")
    val models = listOf("Gemini 1.5 Pro (تبسيط)", "Gemini 1.5 Flash (سريع)", "Tabseet Ultra GPT")

    Text("إعدادات نموذج التوليد", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Model Selection
            Text("النموذج النشط", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                models.forEach { model ->
                    val isSelected = selectedModel == model
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedModel = model }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            model.replace(" (تبسيط)", "").replace(" (سريع)", ""),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tone Selection
            Text("نبرة التوليد الصوتية / المكتوبة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tones.forEach { tone ->
                    val isSelected = selectedTone == tone
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedTone = tone }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tone,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature Slider
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("درجة الإبداع (Temperature)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(String.format("%.1f", temperature), fontSize = 12.sp, color = BrandTiffany, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = temperature,
                onValueChange = { temperature = it },
                colors = SliderDefaults.colors(
                    activeTrackColor = BrandTiffany,
                    inactiveTrackColor = BrandTiffany.copy(alpha = 0.2f),
                    thumbColor = BrandTiffany
                ),
                valueRange = 0.1f..1.0f
            )
        }
    }

    Text("مساحة الأوامر والدخل", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    // Prompt & Action Panel
    OutlinedTextField(
        value = prompt,
        onValueChange = { prompt = it },
        placeholder = { Text("مثال: اكتب لي مقالاً احترافياً عن فوائد استخدام الذكاء الاصطناعي في تمكين الشركات الصغيرة والتوسع...") },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandTiffany,
            unfocusedBorderColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = {
            if (prompt.isBlank() || isLoading) return@Button
            isLoading = true
            scope.launch {
                val fullPrompt = "صغ الإجابة بنبرة $selectedTone وباستخدام إعدادات درجة حرارة $temperature للطلب التالي: $prompt"
                val responseResult = aiService.sendMessage(emptyList(), fullPrompt)
                responseResult.onSuccess {
                    resultText = it
                }.onFailure {
                    resultText = "عذراً، حدث خطأ أثناء محاولة توليد المحتوى: ${it.message}"
                }
                isLoading = false
            }
        },
        enabled = prompt.isNotBlank() && !isLoading,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandTiffany,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("توليد المحتوى بالذكاء الاصطناعي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }

    if (resultText.isNotBlank() || isLoading) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("النتيجة المستخرجة", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isLoading && resultText.isBlank()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = BrandTiffany)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("جاري معالجة الكلمات وتوليد النصوص اللغوية...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                } else {
                    Text(
                        text = resultText,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(resultText))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "نسخ الأوامر", tint = BrandTiffany)
                        }
                        TextButton(onClick = {
                            clipboardManager.setText(AnnotatedString(resultText))
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "نسخ", tint = BrandTiffany)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ سريع", color = BrandTiffany, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 2. Video AI Generator Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAiWorkspace(scope: kotlinx.coroutines.CoroutineScope) {
    var prompt by remember { mutableStateOf("") }
    var aspectSelected by remember { mutableStateOf("16:9") }
    var selectedStyle by remember { mutableStateOf("سينمائي عميق") }
    var videoLength by remember { mutableStateOf("4 ثواني") }
    var renderingProgress by remember { mutableStateOf(0f) }
    var isRendering by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableStateOf(0.4f) }

    val styles = listOf("سينمائي عميق", "أنمي 3D مجسم", "ألوان مائية", "رسم تخطيطي")
    val lengths = listOf("4 ثواني", "8 ثواني", "16 ثانية")

    Text("مواصفات المشهد والمظهر", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Style Grid
            Text("نمط الفيديو", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                styles.forEach { style ->
                    val isSelected = selectedStyle == style
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedStyle = style }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            style,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Aspect Ratio & Duration
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("أبعاد الإطار", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("16:9", "9:16", "1:1").forEach { ratio ->
                            val isSelected = aspectSelected == ratio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { aspectSelected = ratio }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    ratio,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("مدة الإخراج", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        lengths.forEach { length ->
                            val isSelected = videoLength == length
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { videoLength = length }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    length,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Text("صياغة الفكرة والمشهد الإخراجي", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    OutlinedTextField(
        value = prompt,
        onValueChange = { prompt = it },
        placeholder = { Text("مثال: منظر جوي ساحر لمدينة الرياض في الليل مدمجاً بمركبات تحلق في السماء بأسلوب رقمي سيبربانك...") },
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandTiffany,
            unfocusedBorderColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            if (prompt.isBlank() || isRendering) return@Button
            isRendering = true
            isFinished = false
            renderingProgress = 0f
            scope.launch {
                while (renderingProgress < 1f) {
                    delay(30)
                    renderingProgress += 0.015f
                }
                renderingProgress = 1.0f
                delay(300)
                isRendering = false
                isFinished = true
            }
        },
        enabled = prompt.isNotBlank() && !isRendering,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandTiffany,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("بدء توليد فيديو AI السحابي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    if (isRendering) {
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("جاري رندرة الإطارات الفنية... %${(renderingProgress * 100).toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { renderingProgress },
                    color = BrandTiffany,
                    trackColor = BrandTiffany.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("توليد الذكاء الاصطناعي يستغرق بضع ثوانٍ لحساب جودة 4K فائقة الدقة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }

    if (isFinished) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("توليد ناجح للفيديو AI", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                // Main simulated video viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1A1A1A), Color.Black)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                isPlaying = !isPlaying
                                if (isPlaying) {
                                    scope.launch {
                                        while (isPlaying) {
                                            delay(100)
                                            if (videoProgress < 1f) {
                                                videoProgress += 0.01f
                                            } else {
                                                videoProgress = 0f
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(BrandTiffany.copy(alpha = 0.2f))
                                .border(2.dp, BrandTiffany, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = "تشغيل",
                                tint = BrandTiffany,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isPlaying) "جاري تشغيل المعاينة..." else "انقر لتشغيل الفيديو المُوَلّد",
                            fontSize = 12.sp,
                            color = BrandTiffany
                        )
                    }

                    // Progress overlay bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(6.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(videoProgress)
                                .background(BrandTiffany)
                        )
                    }
                }

                // Video info and control options
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("المشهد: $selectedStyle", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("أبعاد الإطار: $aspectSelected | المدة: $videoLength", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = BrandTiffany),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BrandTiffany)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تحميل MP4", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 3. Automations Builder / Workflows Workspace
@Composable
fun AutomationsWorkspace(scope: kotlinx.coroutines.CoroutineScope) {
    var triggerType by remember { mutableStateOf("رسالة واتساب جديدة") }
    var aiModuleType by remember { mutableStateOf("استخراج وتلخيص التفاصيل") }
    var actionType by remember { mutableStateOf("إرسال تقرير لـ Slack") }
    var isWorkflowActive by remember { mutableStateOf(false) }
    var isTestingWorkflow by remember { mutableStateOf(false) }
    var activeTestingStep by remember { mutableStateOf(-1) }

    val triggers = listOf("رسالة واتساب جديدة", "طلب متجر سلة / زد", "حالة نموذج ويب", "بريد إلكتروني وارد")
    val aiModules = listOf("استخراج وتلخيص التفاصيل", "تصنيف العميل والنية", "صياغة رد فوري مناسب")
    val actions = listOf("إرسال تقرير لـ Slack", "تحديث صف التميز بجداول Google", "إشعار بريدي للمشرف")

    Text("تصميم أتمتة تدفق العمل", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Step 1: Trigger Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(BrandTiffany),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("المحفّز الرئيسي (Trigger)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                triggers.take(2).forEach { item ->
                    val isSelected = triggerType == item
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { triggerType = item }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 11.sp, color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 2: AI Action
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(BrandTiffany),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("معالجة الذكاء الاصطناعي (AI Task)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                aiModules.take(2).forEach { item ->
                    val isSelected = aiModuleType == item
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { aiModuleType = item }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 11.sp, color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 3: Destination Action
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(BrandTiffany),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("الإجراء الختامي (Action)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                actions.take(2).forEach { item ->
                    val isSelected = actionType == item
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { actionType = item }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item, fontSize = 11.sp, color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }

    // Interactive Nodes Flowchart Diagram
    Text("الرسم التوضيحي للتدفق والأثمتة الآلية", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Node 1
            NodeItem(
                label = triggerType,
                category = "محفّز الإدخال",
                isActive = activeTestingStep == 0 || isWorkflowActive,
                color = Color(0xFF2196F3)
            )

            // Connection Arrow
            VerticalFlowArrow()

            // Node 2
            NodeItem(
                label = aiModuleType,
                category = "الذكاء الاصطناعي",
                isActive = activeTestingStep == 1 || isWorkflowActive,
                color = BrandTiffany
            )

            // Connection Arrow
            VerticalFlowArrow()

            // Node 3
            NodeItem(
                label = actionType,
                category = "منفذ الإجراءات",
                isActive = activeTestingStep == 2 || isWorkflowActive,
                color = Color(0xFFFF9800)
            )
        }
    }

    // Config Panel
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isWorkflowActive) "التدفق الآلي نشط الآن" else "تفعيل التدفق الآلي",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "معالجة الطلبات بالخلفية فور استحقاق المحفز",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Switch(
                checked = isWorkflowActive,
                onCheckedChange = { isWorkflowActive = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = BrandTiffany,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            if (isTestingWorkflow) return@Button
            isTestingWorkflow = true
            scope.launch {
                activeTestingStep = 0
                delay(1200)
                activeTestingStep = 1
                delay(1200)
                activeTestingStep = 2
                delay(1200)
                activeTestingStep = -1
                isTestingWorkflow = false
            }
        },
        enabled = !isTestingWorkflow,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = BrandTiffany),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isTestingWorkflow) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(color = BrandTiffany, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("محاكاة جريان التدفق...", fontSize = 13.sp)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تجربة ومحاكاة الأتمتة الآن", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun NodeItem(label: String, category: String, isActive: Boolean, color: Color) {
    val animatedBorder = if (isActive) Modifier.border(2.dp, color, RoundedCornerShape(12.dp)) else Modifier
    Box(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .then(animatedBorder)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(category, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun VerticalFlowArrow() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.width(2.dp).height(20.dp).background(BrandTiffany.copy(alpha = 0.4f)))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = BrandTiffany, modifier = Modifier.size(16.dp))
    }
}

// 4. Integrations Ecosystem Portal Hub
@Composable
fun IntegrationsWorkspace(
    scope: kotlinx.coroutines.CoroutineScope,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    // We will display premium integrations toggle widgets
    var isSlackConnected by remember { mutableStateOf(true) }
    var isZapierConnected by remember { mutableStateOf(false) }
    var isWhatsAppConnected by remember { mutableStateOf(false) }
    var isSheetsConnected by remember { mutableStateOf(true) }

    var webhookUrl by remember { mutableStateOf("https://api.tabseet.ai/v1/webhook/usr_x8a2k0lq49s1") }

    Text("صناديق الربط الفوري النشطة", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))

    val integrationList = listOf(
        IntegrationModel("Slack Connect", "إرسال إشعارات فورية لقنوات العمل الخاصة بك", isSlackConnected, Color(0xFF4A154B)) { isSlackConnected = it },
        IntegrationModel("Google Sheets", "حفظ وتصدير المخرجات والبيانات تلقائياً", isSheetsConnected, Color(0xFF0F9D58)) { isSheetsConnected = it },
        IntegrationModel("WhatsApp Business", "إمكانية إرسال الرسائل وتلقي التنبيهات", isWhatsAppConnected, Color(0xFF25D366)) { isWhatsAppConnected = it },
        IntegrationModel("Zapier Portal", "التفاعل مع آلاف التطبيقات عبر Zapier Webhooks", isZapierConnected, Color(0xFFFF4F00)) { isZapierConnected = it }
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        integrationList.forEach { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(app.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(app.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = app.color, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(app.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(app.desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (app.isConnected) BrandTiffany else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (app.isConnected) "متصل" else "مغلق",
                            fontSize = 11.sp,
                            color = if (app.isConnected) BrandTiffany else Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = app.isConnected,
                            onCheckedChange = app.onToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = BrandTiffany
                            )
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Text("رابط عنوان ويب هوك الموحد (Webhook URL)", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("استخدم هذا الرابط الموثق لاستقبال المحفزات من أي خدمة خارجية بنجاح:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = webhookUrl,
                    fontSize = 11.sp,
                    color = BrandTiffany,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "نسخ الرابط",
                    tint = BrandTiffany,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(webhookUrl))
                        }
                )
            }
        }
    }
}

data class IntegrationModel(
    val name: String,
    val desc: String,
    val isConnected: Boolean,
    val color: Color,
    val onToggle: (Boolean) -> Unit
)

// 5. Intelligent Voice Call Center Component & CRM System
data class CRMLead(
    val name: String,
    val phone: String,
    val stage: String, // "جديد" | "مهتم" | "تعاقد" | "مكتمل"
    val lastActive: String,
    val chatHistory: List<CRMMessage>
)

data class CRMMessage(
    val text: String,
    val isUser: Boolean,
    val time: String
)

data class CallHistoryItem(
    val recipient: String,
    val phone: String,
    val time: String,
    val duration: String,
    val status: String, // "success" | "missed"
    val sentiment: String, // "😊 إيجابي" | "😐 محايد" | "😡 غاضب"
    val summary: String
)

@Composable
fun CallCenterWorkspace(scope: kotlinx.coroutines.CoroutineScope) {
    var activeTab by remember { mutableStateOf(0) } // 0: Calls/Dialer, 1: Whatsapp CRM, 2: Integrations

    // Shared accent / Voice Settings
    var selectedAccent by remember { mutableStateOf("اللهجة السعودية") }
    var selectedGender by remember { mutableStateOf("أنثوي (أسيل)") }
    var csatScore by remember { mutableStateOf(97.8f) }

    // --- Tab 0: Phone Call state ---
    var enteredNumber by remember { mutableStateOf("") }
    var isCallActive by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeaker by remember { mutableStateOf(true) }
    var callStatus by remember { mutableStateOf("جاري طلب الرقم...") }
    var callTimerSeconds by remember { mutableStateOf(0) }
    var liveTranscript by remember { mutableStateOf("") }
    var callActiveRecipient by remember { mutableStateOf("عميل مجهول") }

    // Call history log
    val initialCallHistory = listOf(
        CallHistoryItem("الأستاذ متعب المطيري", "0504933219", "اليوم 12:45م", "02:15 دقيقة", "success", "😊 إيجابي", "طلب أتمتة ربط قنوات الإعلانات بنظام تبسيط لإرسال تقارير يومية."),
        CallHistoryItem("مؤسسة الرويلي العقارية", "0554192033", "اليوم 10:11ص", "01:04 دقيقة", "success", "😐 محايد", "تم استعراض قالب سيناريو الفيديو لتسويق المجمع السكني وبانتظار الموافقة."),
        CallHistoryItem("صالون لمى للتجميل", "0548123984", "أمس 04:22م", "00:00 دقيقة", "missed", "😐 معلق", "مكالمة فائتة لم يتم الرد عليها من العميل."),
        CallHistoryItem("فيصل الشهري (متجر طويق)", "0561122394", "أمس 02:15م", "03:45 دقيقة", "success", "😊 إيجابي", "تم الاتفاق على باقة التوليد الاحترافي للفيديو الإعلاني وإرسال رابط الفاتورة.")
    )
    val callHistoryList = remember { mutableStateListOf(*initialCallHistory.toTypedArray()) }

    // Increment simulated call timer
    LaunchedEffect(isCallActive) {
        if (isCallActive) {
            callTimerSeconds = 0
            callStatus = "جاري طلب الرقم..."
            delay(1500)
            callStatus = "يرن الهاتف..."
            delay(1500)
            callStatus = "متصل وسامع 🟢"
            
            val livePhrases = listOf(
                "أهلاً بك، أنا المستشار الذكي لمنصة تبسيط. كيف يمكنني مساعدتكم اليوم؟",
                "العميل: حياك الله، كنت أستفسر عن واجهة برمجة التطبيقات وسرعة معالجة الفيديو.",
                "المستشار الذكي: ممتاز، منصة تبسيط توفر توليداً فائق السرعة عبر خوادم جيميناي مخصصة.",
                "العميل: رائع جداً، وسؤالي عن إمكانية تفعيل الـ Webhook لإرسال النتيجة فوراً لقسم المبيعات لدينا؟",
                "المستشار الذكي: نعم بالضبط، يمكنك ضبط رابط الويب هوك لإرسال ملف الفيديو والسيناريو خلال ثوانٍ معدودة."
            )
            
            liveTranscript = ""
            var phraseIndex = 0
            
            while (isCallActive) {
                delay(1000)
                callTimerSeconds++
                
                // Simulate dialog transcription appearing
                if (callTimerSeconds % 4 == 0 && phraseIndex < livePhrases.size) {
                    liveTranscript += livePhrases[phraseIndex] + "\n\n"
                    phraseIndex++
                }
            }
        }
    }

    // --- Tab 1: CRM & Whatsapp state ---
    var selectedLeadIndex by remember { mutableStateOf(0) }
    var searchCRMQuery by remember { mutableStateOf("") }
    var currentCRMMessageField by remember { mutableStateOf("") }
    var lastInteractedStage by remember { mutableStateOf("") }

    val initialLeads = listOf(
        CRMLead(
            name = "الأستاذ متعب المطيري",
            phone = "0504933219",
            stage = "جديد",
            lastActive = "شمال الرياض - مهتم بخدمات توليد الأفلام القصيرة",
            chatHistory = listOf(
                CRMMessage("مرحباً بك في منصة تبسيط، يسعدنا تواصلك معنا لتسهيل صناعة السيناريوهات.", false, "12:30م"),
                CRMMessage("أهلاً، أرغب بتجربة أداة الكول سنتر وأتمتة المكالمات.", true, "12:31م")
            )
        ),
        CRMLead(
            name = "مؤسسة الرويلي العقارية",
            phone = "0554192033",
            stage = "مهتم",
            lastActive = "متابعة ترويجية - رغبة في سيناريو فيديو عقاري",
            chatHistory = listOf(
                CRMMessage("تم إرسال السيناريو المولد للفيلم الكرتوني العقاري.", false, "أمس"),
                CRMMessage("شاهدت المسودة وهي جيدة ولكن أرغب في تعديل الكوامرة.", true, "أمس")
            )
        ),
        CRMLead(
            name = "المصمم إياد المطيري",
            phone = "0548877112",
            stage = "تعاقد",
            lastActive = "انتظار توقيع عقد باقة تبسيط السنوية اللامحدودة",
            chatHistory = listOf(
                CRMMessage("تم إرسال مسودة الاتفاقية والبنود القانونية والمالية.", false, "الأحد"),
                CRMMessage("سأراجعها اليوم مع الإدارة وبإذن الله نغلق الصفقة.", true, "الأحد")
            )
        ),
        CRMLead(
            name = "عقارات محمد الفيصل",
            phone = "0567221199",
            stage = "مكتمل",
            lastActive = "تفعيل باقة دمج الذكاء الاصطناعي مع المكالمات",
            chatHistory = listOf(
                CRMMessage("تم استلام مبلغ الاشتراك وتفعيل لوحة الكول سنتر.", false, "الخميس"),
                CRMMessage("خدمة ودعم ممتازين، شكراً جزيلاً لكم ولتكاملاتكم.", true, "الخميس")
            )
        )
    )

    // Using a list representing CRM state
    val leadsStateList = remember { mutableStateListOf(*initialLeads.toTypedArray()) }

    // Simulated customer reply delay
    var isSimulatingCustomerReply by remember { mutableStateOf(false) }
    val currentContext = androidx.compose.ui.platform.LocalContext.current

    var dialedNumberToRequest by remember { mutableStateOf("") }
    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val targetNum = dialedNumberToRequest.ifBlank { enteredNumber.ifBlank { "0504933219" } }
        val intent = if (isGranted) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$targetNum"))
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$targetNum"))
        }
        try {
            currentContext.startActivity(intent)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    // --- Tab 2: Integrations settings ---
    var twilioEnabled by remember { mutableStateOf(true) }
    var whatsappBusinessEnabled by remember { mutableStateOf(true) }
    var crmSyncEnabled by remember { mutableStateOf(true) }
    var voiceGenerationEnabled by remember { mutableStateOf(false) }
    var webhookUrlInput by remember { mutableStateOf("https://api.tabseet.ai/v1/webhooks/voice") }
    var showWebhookTestResult by remember { mutableStateOf(false) }
    var webhookPayloadLog by remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxWidth()) {
        // Styled Platform Sub-tabs Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color(0xFFF1F6F4), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                Triple(0, "📞 كول سنتر و اتصال", Icons.Default.Call),
                Triple(1, "💬 واتساب و CRM", Icons.AutoMirrored.Filled.List),
                Triple(2, "⚙️ تكاملات الأنظمة", Icons.Default.Settings)
            ).forEach { (index, title, icon) ->
                val isSelected = activeTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BrandTiffany else Color.Transparent)
                        .clickable { activeTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // --- TAB 0: INTERACTIVE CALL CENTER & DIALER ---
        if (activeTab == 0) {
            // Stats Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("المكالمات الفعالة لليوم", "24 مكالمة", Color(0xFF07211D)),
                    Triple("نسبة إتمام الاتصال للذكاء", "98.9%", BrandTiffany),
                    Triple("نقاط الرضا CSAT", "%$csatScore", Color(0xFF4CAF50))
                ).forEach { (label, value, color) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(label, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
                        }
                    }
                }
            }

            // Quick Tone and Accent Settings Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Selector 1: Accent
                Column(modifier = Modifier.weight(1f)) {
                    Text("لهجة الذكاء الاصطناعي", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F6F4), RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("اللهجة السعودية", "اللهجة المصرية").forEach { accent ->
                            val isSel = selectedAccent == accent
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color.White else Color.Transparent)
                                    .clickable { selectedAccent = accent }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(accent, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSel) BrandTiffany else Color.Gray)
                            }
                        }
                    }
                }

                // Selector 2: Voice Gender
                Column(modifier = Modifier.weight(1f)) {
                    Text("جنس الصوت والنبرة", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F6F4), RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("أنثوي (أسيل)", "ذكوري (عقبة)").forEach { voice ->
                            val isSel = selectedGender == voice
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color.White else Color.Transparent)
                                    .clickable { selectedGender = voice }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(voice, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSel) BrandTiffany else Color.Gray)
                            }
                        }
                    }
                }
            }

            // Interactive Live Call view or Phone Dialer Row
            if (isCallActive) {
                // Active Call View Overlay
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF07211D)), // Dark premium background
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandTiffany.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "بث كول سنتر مباشر",
                                    color = BrandTiffany,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسجيل نشط", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Callee Avatar and Details
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = BrandTiffany, modifier = Modifier.size(32.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(callActiveRecipient, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(enteredNumber.ifBlank { "0504933219" }, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Call duration timer
                        val min = callTimerSeconds / 60
                        val sec = callTimerSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", min, sec),
                            color = BrandTiffany,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(callStatus, color = Color.Gray, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live Pulse/Equalizer Animation Representation
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(30.dp)
                        ) {
                            repeat(12) { i ->
                                val waveHeight = remember { mutableStateOf(10.dp) }
                                LaunchedEffect(isCallActive) {
                                    while (isCallActive) {
                                        delay((100..400).random().toLong())
                                        waveHeight.value = (5..28).random().dp
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(waveHeight.value)
                                        .background(BrandTiffany, RoundedCornerShape(1.5.dp))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Live audio transcription box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = if (liveTranscript.isNotBlank()) liveTranscript else "يرجى التحدث لمشاهدة تفريغ المحادثة في الوقت الحقيقي عبر نموذج تبسيط لجيميناي...",
                                fontSize = 11.sp,
                                color = Color.White,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Call control row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mute button
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isMuted) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "كتم",
                                    tint = if (isMuted) Color.Red else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Disconnect/Hangup action Button
                            Button(
                                onClick = {
                                    isCallActive = false
                                    // Append this simulated call to call logs
                                    val finalDur = String.format("%02d:%02d", callTimerSeconds / 60, callTimerSeconds % 60)
                                    callHistoryList.add(
                                        0,
                                        CallHistoryItem(
                                            recipient = callActiveRecipient,
                                            phone = enteredNumber.ifBlank { "0504933219" },
                                            time = "الآن",
                                            duration = finalDur,
                                            status = "success",
                                            sentiment = "😊 إيجابي",
                                            summary = "اتصال من الكول سنتر لمتابعة الاستفسار. أبدى العميل رغبة بتبسيط أتمتة الرسائل ودمجها مع قنوات الواتساب CRM."
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.height(44.dp),
                                shape = RoundedCornerShape(22.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CallEnd, contentDescription = "إنهاء", tint = Color.White)
                                    Text("إنهاء المكالمة الذكية", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Speaker button
                            IconButton(
                                onClick = { isSpeaker = !isSpeaker },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaker) BrandTiffany.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "مكبر الصوت",
                                    tint = if (isSpeaker) BrandTiffany else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Interactive Dialer Pad Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "مركّب الأرقام والاتصال الهاتفي من الجوال",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF07211D),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Selector for caller trunk
                        var showTrunkMenu by remember { mutableStateOf(false) }
                        var selectedTrunk by remember { mutableStateOf("الرقم الموحد الأساسي (9200)") }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F6F4), RoundedCornerShape(8.dp))
                                .clickable { showTrunkMenu = !showTrunkMenu }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SettingsPhone, contentDescription = null, tint = BrandTiffany, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(selectedTrunk, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF07211D))
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                        }
                        
                        if (showTrunkMenu) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6F4)),
                                border = BorderStroke(1.dp, Color(0xFFE2ECE9))
                            ) {
                                Column {
                                    listOf("الرقم الموحد الأساسي (9200)", "رقم فرع الرياض المخصص", "رقم فرع جدة الدولي").forEach { trunk ->
                                        Text(
                                            text = trunk,
                                            fontSize = 10.sp,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedTrunk = trunk
                                                    showTrunkMenu = false
                                                }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Phone Number input box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color(0xFFF1F6F4), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2ECE9), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = enteredNumber.ifBlank { "أدخل رقم هاتف العميل للتجربة..." },
                                modifier = Modifier.weight(1f),
                                color = if (enteredNumber.isEmpty()) Color.Gray else Color(0xFF07211D),
                                fontSize = 14.sp,
                                fontWeight = if (enteredNumber.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                                textAlign = TextAlign.Left
                            )
                            if (enteredNumber.isNotEmpty()) {
                                IconButton(onClick = { enteredNumber = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dial Keypad Grid (3x4)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val rows = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("*", "0", "#")
                            )
                            for (row in rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (key in row) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF7F9F8))
                                                .clickable { enteredNumber += key }
                                                .border(1.dp, Color(0xFFE2ECE9).copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = key,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF07211D)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Connect and Direct Call deep link Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Direct system phone call trigger deep link
                            Button(
                                onClick = {
                                    val targetNum = enteredNumber.ifBlank { "0504933219" }
                                    dialedNumberToRequest = targetNum
                                    val hasCallPermission = ContextCompat.checkSelfPermission(
                                        currentContext,
                                        android.Manifest.permission.CALL_PHONE
                                    ) == PackageManager.PERMISSION_GRANTED
                                    
                                    if (hasCallPermission) {
                                        val intent = Intent(Intent.ACTION_CALL).apply {
                                            data = Uri.parse("tel:$targetNum")
                                        }
                                        try {
                                            currentContext.startActivity(intent)
                                        } catch (e: Exception) {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:$targetNum")
                                            }
                                            try { currentContext.startActivity(dialIntent) } catch (err: Exception) {}
                                        }
                                    } else {
                                        callPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07211D)),
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Text("اتصال جوال 📱", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            // 2. Simulated AI Call
                            Button(
                                onClick = {
                                    callActiveRecipient = "الأستاذ متعب المطيري"
                                    isCallActive = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                                modifier = Modifier.weight(1.2f).height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Text("اتصال AI ذكي 🤖", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Recent Calls Logs Section
            Text("سجل المكالمات الأخير وتحليل الملاحظات", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF07211D), modifier = Modifier.padding(bottom = 8.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                callHistoryList.forEach { call ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F6F4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (call.status == "success") Icons.Default.CallReceived else Icons.Default.CallMissed,
                                            contentDescription = null,
                                            tint = if (call.status == "success") BrandTiffany else Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(call.recipient, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF07211D))
                                        Text("${call.phone} • ${call.time}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (call.status == "success") BrandTiffany.copy(alpha = 0.12f) else Color.Red.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = call.duration,
                                            fontSize = 9.sp,
                                            color = if (call.status == "success") BrandTiffany else Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(call.sentiment, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Call notes summary compiled by Gemini
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF7F9F8), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Notes, contentDescription = null, tint = BrandTiffany, modifier = Modifier.size(12.dp).padding(top = 2.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = call.summary,
                                        fontSize = 10.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- TAB 1: WHATSAPP CRM & PIPELINE GATEWAY ---
        if (activeTab == 1) {
            // Horizontal Pipeline CRM Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("جديد 🆕", 1),
                    Pair("مهتم ⭐", 1),
                    Pair("تعاقد ✍️", 1),
                    Pair("تم البيع 🤝", 1)
                ).forEach { (stage, count) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6F4)),
                        border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stage, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF07211D))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(BrandTiffany, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$count", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Lead search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(Color(0xFFF1F6F4), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "البحث السريع في عملاء المنظومة...",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }

            // CRM Lead selector List
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadsStateList.forEachIndexed { index, lead ->
                    val isLeadSel = selectedLeadIndex == index
                    Card(
                        modifier = Modifier
                            .width(140.dp)
                            .clickable { selectedLeadIndex = index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLeadSel) BrandTiffany.copy(alpha = 0.12f) else Color.White
                        ),
                        border = BorderStroke(
                            width = 1.6.dp,
                            color = if (isLeadSel) BrandTiffany else Color(0xFFE2ECE9)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lead.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF07211D),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            when (lead.stage) {
                                                "جديد" -> Color.Blue
                                                "مهتم" -> Color.Magenta
                                                "تعاقد" -> Color(0xFFFF9800)
                                                else -> Color(0xFF4CAF50)
                                            },
                                            CircleShape
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(lead.phone, fontSize = 9.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF07211D).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = lead.stage,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF07211D),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Interactive WhatsApp CRM Chat Sandbox
            val currentLead = leadsStateList.getOrNull(selectedLeadIndex)
            if (currentLead != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // WhatsApp Style Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF07211D))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = BrandTiffany, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(currentLead.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("${currentLead.phone} • نشط الآن على الواتس", fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                                    }
                                }
                                
                                // Real Quick System Redirect Shortcuts (tel & whatsapp web)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Deep Link: WhatsApp api link
                                    IconButton(
                                        onClick = {
                                            try {
                                                val intent = android.content.Intent(
                                                    android.content.Intent.ACTION_VIEW,
                                                    android.net.Uri.parse("https://wa.me/" + currentLead.phone.replace("[^0-9]".toRegex(), ""))
                                                )
                                                currentContext.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Message, contentDescription = "واتس حقيقي", tint = BrandTiffany, modifier = Modifier.size(16.dp))
                                    }
                                    
                                    // Deep Link: system call
                                    IconButton(
                                        onClick = {
                                            val targetNum = currentLead.phone
                                            dialedNumberToRequest = targetNum
                                            val hasCallPermission = ContextCompat.checkSelfPermission(
                                                currentContext,
                                                android.Manifest.permission.CALL_PHONE
                                            ) == PackageManager.PERMISSION_GRANTED
                                            
                                            if (hasCallPermission) {
                                                val intent = Intent(Intent.ACTION_CALL).apply {
                                                    data = Uri.parse("tel:$targetNum")
                                                }
                                                try {
                                                    currentContext.startActivity(intent)
                                                } catch (e: Exception) {
                                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                        data = Uri.parse("tel:$targetNum")
                                                    }
                                                    try { currentContext.startActivity(dialIntent) } catch (err: Exception) {}
                                                }
                                            } else {
                                                callPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                                            }
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.SettingsPhone, contentDescription = "اتصال حقيقي", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // Info Alert Stage Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF9C4)) // Warning label block
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoGraph, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("الموقع الجغرافي وحالة الملف: ${currentLead.lastActive}", fontSize = 9.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                                }
                                
                                // Direct Stage adjustment
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("تغيير المرحلة:", fontSize = 8.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    listOf("جديد", "مهتم", "تعاقد", "مكتمل").forEach { st ->
                                        val isStSel = currentLead.stage == st
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(if (isStSel) BrandTiffany else Color.LightGray.copy(alpha = 0.5f))
                                                .clickable {
                                                    val updatedLead = currentLead.copy(stage = st)
                                                    leadsStateList[selectedLeadIndex] = updatedLead
                                                }
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(st, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = if (isStSel) Color.White else Color.Black)
                                        }
                                    }
                                }
                            }
                        }

                        // WhatsApp Chat log Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color(0xFFE5DDD5)) // WhatsApp vintage color
                                .padding(12.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(currentLead.chatHistory) { msg ->
                                    val isOut = !msg.isUser
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (isOut) Arrangement.End else Arrangement.Start
                                    ) {
                                        Card(
                                            shape = RoundedCornerShape(
                                                topStart = 8.dp,
                                                topEnd = 8.dp,
                                                bottomStart = if (isOut) 8.dp else 0.dp,
                                                bottomEnd = if (isOut) 0.dp else 8.dp
                                            ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isOut) Color(0xFFDCF8C6) else Color.White
                                            ),
                                            border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.4f)),
                                            modifier = Modifier.widthIn(max = 220.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(msg.text, fontSize = 11.sp, color = Color.Black)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    modifier = Modifier.align(Alignment.End),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(msg.time, fontSize = 7.sp, color = Color.Gray)
                                                    if (isOut) {
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color(0xFF34B7F1), modifier = Modifier.size(10.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Dynamic automated thinking loader indicator
                            if (isSimulatingCustomerReply) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                        .background(Color.White, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("يرد العميل الآن بالأتمتة... ✍️", fontSize = 9.sp, color = BrandTiffany)
                                }
                            }
                        }

                        // CRM Template quick replies row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F6F4))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("💡 نماذج الرد الأوتوماتيكية:", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "ترحيب بالعميل الجديد 🌟" to "مرحباً بك مجدداً، لقد تواصلنا معك بخصوص باقة تبسيط. كيف يمكننا خدمتك اليوم؟",
                                        "إرسال رابط الفاتورة والدفع 💳" to "أهلاً بك، تم إصدار رابط التسلسلات للفاتورة الخاصة بك، يمكنك الدفع مباشرة: payment.tabseet.ai/inv_24",
                                        "رابط عرض الأسعار ومسودة السيناريو 📋" to "لقد انتهى مساعد تبسيط الذكي من صياغة السيناريو الخاص بك، يمكنك مراجعته الآن وتأكيد كوادره.",
                                        "طلب تقييم الدعم الفني ⭐" to "بناءً على مكالمتنا السابقة، نرجو التفضل بتقييم خدمات الكول سنتر الموحد لتبسيط."
                                    ).forEach { (label, filledText) ->
                                        Card(
                                            modifier = Modifier.clickable {
                                                currentCRMMessageField = filledText
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            border = BorderStroke(1.dp, Color(0xFFE2ECE9))
                                        ) {
                                            Text(label, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF07211D))
                                        }
                                    }
                                }
                            }
                        }

                        // WhatsApp Input Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFF1F6F4))
                                    .border(1.dp, Color(0xFFE2ECE9), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                if (currentCRMMessageField.isEmpty()) {
                                    Text("اكتب رسالة واتساب للعميل...", color = Color.Gray, fontSize = 11.sp)
                                }
                                androidx.compose.foundation.text.BasicTextField(
                                    value = currentCRMMessageField,
                                    onValueChange = { currentCRMMessageField = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = Color.Black)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(6.dp))

                            // Send message button
                            IconButton(
                                onClick = {
                                    if (currentCRMMessageField.isNotBlank()) {
                                        val typedText = currentCRMMessageField
                                        val newMsg = CRMMessage(typedText, false, "الآن")
                                        val updatedHistory = currentLead.chatHistory + newMsg
                                        leadsStateList[selectedLeadIndex] = currentLead.copy(chatHistory = updatedHistory)
                                        currentCRMMessageField = ""

                                        // Simulate client reply after brief thinking delay
                                        isSimulatingCustomerReply = true
                                        scope.launch {
                                            delay(3000)
                                            isSimulatingCustomerReply = false
                                            val simulatedReplies = listOf(
                                                "تسلم يا غالي، جربت الرابط وشغال تمام. سأنتهي من الدفع اليوم.",
                                                "جميل جداً! هل هناك باقة مخصصة لوكالات الإنتاج الإعلاني لدمج 10 خطوط كول سنتر؟",
                                                "قرأت الشروط وهي تفوق التوقعات، شكراً لتجاوب مستشاري تبسيط السريع.",
                                                "تم تعديل الملاحظة، شكراً للدعم والمتابعة الرائعة."
                                            )
                                            val clientMsg = CRMMessage(simulatedReplies.random(), true, "الآن")
                                            val currentLeadInst = leadsStateList.getOrNull(selectedLeadIndex)
                                            if (currentLeadInst != null) {
                                                val finalHistory = currentLeadInst.chatHistory + clientMsg
                                                leadsStateList[selectedLeadIndex] = currentLeadInst.copy(chatHistory = finalHistory)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF25D366), CircleShape) // WhatsApp visual green
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "ارسال", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // --- TAB 2: ADVANCED INTEGRATIONS CONFIGS ---
        if (activeTab == 2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("بوابات الكول سنتر المتكاملة وتفعيلها", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF07211D))
                    Text("قم بتمويل وربط خطوط اتصالك وخدمات المراسلة في لوحة واحدة للتحكم الكامل.", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

                    // Gateway 1
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تفعيل بوابة Twilio VoIP للاتصال الهاتفي", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF07211D))
                            Text("ربط الخطوط الأرضية 9200 وتخصيص قنوات التحدث.", fontSize = 9.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = twilioEnabled,
                            onCheckedChange = { twilioEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandTiffany, checkedTrackColor = BrandTiffany.copy(alpha = 0.4f))
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2ECE9).copy(alpha = 0.5f))

                    // Gateway 2
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ربط سحابي لوكلاء WhatsApp Cloud API", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF07211D))
                            Text("مزامنة CRM المباشرة وتلقي الرسائل وتمريرها تلقائياً.", fontSize = 9.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = whatsappBusinessEnabled,
                            onCheckedChange = { whatsappBusinessEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandTiffany, checkedTrackColor = BrandTiffany.copy(alpha = 0.4f))
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2ECE9).copy(alpha = 0.5f))

                    // Gateway 3
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("مزامنة فورية مع Salesforce & HubSpot CRM", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF07211D))
                            Text("تسجيل المكالمات والسيناريوهات المولد وتحديث حالة العميل فوريا.", fontSize = 9.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = crmSyncEnabled,
                            onCheckedChange = { crmSyncEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandTiffany, checkedTrackColor = BrandTiffany.copy(alpha = 0.4f))
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2ECE9).copy(alpha = 0.5f))

                    // Gateway 4
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("صوت ElevenLabs الذكي فائق جودة الرندرة AI", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF07211D))
                            Text("محرك عاطفي وعفوي يحاكي لهجة العميل بدقة فائقة.", fontSize = 9.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = voiceGenerationEnabled,
                            onCheckedChange = { voiceGenerationEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandTiffany, checkedTrackColor = BrandTiffany.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            // Real Time Webhook Simulation Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("لوحة ضبط واستقبال الويب هوك Webhook Endpoint", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF07211D))
                    Text("ستقوم منصة تبسيط بإشعار خوادمك فور إتمام المكالمة للعميل أو تسليمه فاتورة.", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F6F4), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE2ECE9), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = BrandTiffany, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = webhookUrlInput,
                            onValueChange = { webhookUrlInput = it },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = Color(0xFF07211D), fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            webhookPayloadLog = """
                            {
                              "event": "voice_call.completed",
                              "timestamp": ${System.currentTimeMillis()},
                              "call_id": "tabseet_call_v4_${(1000..9999).random()}",
                              "caller_trunk": "920015124",
                              "callee": "+966504933219",
                              "duration_seconds": 135,
                              "ai_summary": "العميل أبدى حماس شديد لتفعيل باقة تبسيط السنوية اللامحدودة. يرجى المتابعة لإتمام العقد وتثبيت الوصف البصري.",
                              "crm_sync": {
                                "status": "processed",
                                "deal_stage": "contracting_proposal"
                              }
                            }
                            """.trimIndent()
                            showWebhookTestResult = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إرسال فوري لحمولة Webhook تجريبية 🚀", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (showWebhookTestResult) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            color = Color(0xFF07211D),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandTiffany.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Payload المرسل (HTTP POST)", color = BrandTiffany, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { showWebhookTestResult = false },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Text(
                                    text = webhookPayloadLog,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

