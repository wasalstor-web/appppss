package com.example.presentation.screens.chat

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.repository.ChatRepository
import com.example.domain.model.Message
import com.example.domain.model.Role
import com.example.domain.repository.AiService
import com.example.ui.theme.BrandTiffany
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.UUID

@Composable
fun uriToBitmap(uriString: String?): ImageBitmap? {
    if (uriString == null) return null
    val context = LocalContext.current
    return remember(uriString) {
        try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    navController: NavController,
    chatRepository: ChatRepository,
    aiService: AiService
) {
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(conversationId, chatRepository, aiService) as T
        }
    }
    val viewModel: ChatViewModel = viewModel(factory = factory)
    
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    
    // Status message toast helper
    var showCopyFeedback by remember { mutableStateOf(false) }
    var isWebSimulatorMode by remember { mutableStateOf(true) }

    // Media and voice indicators
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentAudioUri by remember { mutableStateOf<String?>(null) }
    
    var showAudioRecorderModal by remember { mutableStateOf(false) }
    var isRecordingSimulated by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableStateOf(0) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    // Interactive simulated recording timer
    LaunchedEffect(isRecordingSimulated) {
        if (isRecordingSimulated) {
            recordingSeconds = 0
            while (isRecordingSimulated) {
                delay(1000)
                recordingSeconds++
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Color definitions
    val darkGreenBg = Color(0xFF07211D)
    val lightSageBg = Color(0xFFF7F9F8)
    val inputSage = Color(0xFFF1F6F4)
    val lightBorder = Color(0xFFE2ECE9)

    val scaffoldContent = @Composable { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Quick Suggestion Bar at Top
                AnimatedVisibility(
                    visible = messages.isEmpty() && selectedImageUri == null && attachmentAudioUri == null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "💡 مقترحات سريعة للبدء:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkGreenBg,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "اكتب لي سيناريو فيديو تسويقي لشركة مبسط",
                                "صمم موجز إنتاج فيديو سينمائي 4K",
                                "خطط فكرة أتمتة لشركة ريادية",
                                "كيف يمكن دمج نماذج جيميناي لتوليد النصوص؟"
                            ).forEach { prompt ->
                                Card(
                                    modifier = Modifier
                                        .clickable {
                                            inputText = prompt
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = inputSage),
                                    border = BorderStroke(1.dp, lightBorder)
                                ) {
                                    Text(
                                        text = prompt,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = darkGreenBg
                                    )
                                }
                            }
                        }
                    }
                }

                // Chat Messages Space
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        if (messages.isEmpty()) {
                            item {
                                EmptyWelcomeBox(darkGreenBg)
                            }
                        }
                        
                        items(messages) { msg ->
                            MessageBubble(
                                message = msg,
                                accentColor = BrandTiffany,
                                darkGreenBg = darkGreenBg,
                                onCopyClick = {
                                    clipboardManager.setText(AnnotatedString(msg.text))
                                    coroutineScope.launch {
                                        showCopyFeedback = true
                                        delay(1500)
                                        showCopyFeedback = false
                                    }
                                }
                            )
                        }
                        
                        // Beautiful sequential loading steps
                        if (isLoading) {
                            item {
                                ChatLoaderSteps(BrandTiffany, darkGreenBg)
                            }
                        }
                    }
                }

                // Preview Attachment Row above Input
                if (selectedImageUri != null || attachmentAudioUri != null) {
                    Surface(
                        color = Color(0xFFF1F6F4),
                        border = BorderStroke(1.dp, lightBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            selectedImageUri?.let { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, lightBorder, RoundedCornerShape(8.dp))
                                ) {
                                    val bmp = uriToBitmap(uri.toString())
                                    if (bmp != null) {
                                        Image(
                                            bitmap = bmp,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Image,
                                            contentDescription = null,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                    IconButton(
                                        onClick = { selectedImageUri = null },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .background(Color.Red, CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "طلب إلغاء",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            attachmentAudioUri?.let { audio ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, lightBorder),
                                    modifier = Modifier.height(60.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = BrandTiffany
                                        )
                                        Text(
                                            text = "تسجيل لـمُبَسَّط.mp3",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = darkGreenBg
                                        )
                                        IconButton(
                                            onClick = { attachmentAudioUri = null },
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "حذف التسجيل",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "مرفقات جاهزة للإرسال ✨",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Input Layout Box (matching creative studio aesthetics)
                Surface(
                    color = Color.White,
                    border = BorderStroke(1.dp, lightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sound/Mic helper with Long/Short Tap flexibility
                        IconButton(
                            onClick = { showAudioRecorderModal = true },
                            modifier = Modifier
                               .size(36.dp)
                               .background(inputSage, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic, 
                                contentDescription = "تحدث أو سجل", 
                                tint = darkGreenBg,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Photo Picker Button
                        IconButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                               .size(36.dp)
                               .background(inputSage, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate, 
                                contentDescription = "إرفاق صورة", 
                                tint = darkGreenBg,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Custom Styled Creative Input
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(inputSage)
                                .border(1.dp, lightBorder, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            if (inputText.isEmpty()) {
                                Text(
                                    "اكتب رسالة أو اطلب تصميم سيناريو...",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                            BasicTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                textStyle = TextStyle(
                                    color = darkGreenBg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.SansSerif
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 120.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Styled Send Action in Tiffany Green Circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isBlank() && selectedImageUri == null && attachmentAudioUri == null) {
                                        Color.Gray.copy(alpha = 0.3f)
                                    } else {
                                        darkGreenBg
                                    }
                                )
                                .clickable(
                                    enabled = inputText.isNotBlank() || selectedImageUri != null || attachmentAudioUri != null
                                ) {
                                    viewModel.sendMessage(
                                        text = inputText,
                                        imageUri = selectedImageUri?.toString(),
                                        audioUri = attachmentAudioUri
                                    )
                                    inputText = ""
                                    selectedImageUri = null
                                    attachmentAudioUri = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send, 
                                contentDescription = "إرسال", 
                                tint = if (inputText.isBlank() && selectedImageUri == null && attachmentAudioUri == null) {
                                    Color.White.copy(alpha = 0.5f)
                                } else {
                                    BrandTiffany
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Copy feedback toast
            AnimatedVisibility(
                visible = showCopyFeedback,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color = darkGreenBg,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BrandTiffany),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "📋 تم نسخ نص الرد إلى الحافظة بنجاح",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Beautiful interactive simulator voice recorder dialog
            if (showAudioRecorderModal) {
                AlertDialog(
                    onDismissRequest = { showAudioRecorderModal = false },
                    title = {
                        Text(
                            "🎙️ مسجل الوجدان الصوتي لمنصة مُبَسَّط", 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "تحدث بسلاسة لأتمتة مشاريعك، أو أمل رسالة كاملة لصياغتها بالذكاء التوليدي.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            // Voice Audio Level Visualizer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isRecordingSimulated) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Dynamic pulsing visual lines representing the audio wavelength
                                        repeat(9) { index ->
                                            val pulseScale = remember { mutableStateOf(1f) }
                                            LaunchedEffect(isRecordingSimulated) {
                                                while (isRecordingSimulated) {
                                                    delay(java.util.concurrent.ThreadLocalRandom.current().nextLong(100, 300))
                                                    pulseScale.value = (20..150).shuffled().first().toFloat() / 100f
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .height(40.dp * pulseScale.value)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(BrandTiffany, darkGreenBg)
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(inputSage, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            // Dynamic Timer Status
                            Text(
                                text = if (isRecordingSimulated) {
                                    String.format("جاري التسجيل... %02d:%02d 🎙️", recordingSeconds / 60, recordingSeconds % 60)
                                } else {
                                    "اضغط على زر البدء لتبسيط فكرتك صوتياً"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isRecordingSimulated) BrandTiffany else Color.Gray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // Standard Android System Intent Speech recognition
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                                        }
                                        try {
                                            speechRecognizerLauncher.launch(intent)
                                            showAudioRecorderModal = false
                                        } catch (e: Exception) {}
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = darkGreenBg),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Hearing, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(14.dp), 
                                            tint = Color.White
                                        )
                                        Text("إملاء فوري للرسالة", fontSize = 10.sp, color = Color.White)
                                    }
                                }

                                if (!isRecordingSimulated) {
                                    Button(
                                        onClick = { isRecordingSimulated = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("البدء بتسجيل المرفق", fontSize = 10.sp, color = Color.White)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            isRecordingSimulated = false
                                            attachmentAudioUri = "simulated_voice_clip_" + UUID.randomUUID().toString() + ".mp3"
                                            showAudioRecorderModal = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("إنهاء وتثبيت الصوت ✅", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { 
                            isRecordingSimulated = false
                            showAudioRecorderModal = false 
                        }) {
                            Text("إلغاء", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                )
            }
        }
    }

    if (isWebSimulatorMode) {
        com.example.presentation.components.WebBrowserShell(
            navController = navController,
            currentUrl = "/chat/$conversationId",
            pageTitle = "مساعد توليد الفيديو الإبداعي"
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366))
                                    )
                                    Text(
                                        "مُبَسَّط AI Studio", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    "مُساعِد الإبداع وتوليد الرسوم والسيناريوهات الكبيرة",
                                    fontSize = 10.sp,
                                    color = BrandTiffany,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                    contentDescription = "رجوع", 
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            Button(
                                onClick = { isWebSimulatorMode = !isWebSimulatorMode },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = if (isWebSimulatorMode) "الوضع الجوال 📱" else "وضع الويب اب 🌐",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = {
                                inputText = ""
                                selectedImageUri = null
                                attachmentAudioUri = null
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "تنظيف", tint = BrandTiffany)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = darkGreenBg,
                            titleContentColor = Color.White
                        )
                    )
                },
                containerColor = lightSageBg
            ) { paddingValues ->
                scaffoldContent(paddingValues)
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                )
                                Text(
                                    "مُبَسَّط AI Studio", 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                "تحكم ويب مستقل بالذكاء التوليدي",
                                fontSize = 10.sp,
                                color = BrandTiffany,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "رجوع", 
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = { isWebSimulatorMode = !isWebSimulatorMode },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (isWebSimulatorMode) "الوضع الجوال 📱" else "وضع الويب اب 🌐",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = {
                            inputText = ""
                            selectedImageUri = null
                            attachmentAudioUri = null
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "تنظيف", tint = BrandTiffany)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = darkGreenBg,
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = lightSageBg
        ) { paddingValues ->
            scaffoldContent(paddingValues)
        }
    }
}

@Composable
fun EmptyWelcomeBox(themeColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2ECE9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(BrandTiffany.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome, 
                    contentDescription = null, 
                    tint = themeColor, 
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "مرحباً بك في مُساعد مُبَسَّط",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = themeColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "أنا هنا لمساعدتك في توليد السيناريوهات المميزة وتصميم لقطات الفيديو والتحكم الشامل وتلقي صور ومقاطع المشاريع وبنائها.",
                color = Color.Gray,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    accentColor: Color,
    darkGreenBg: Color,
    onCopyClick: () -> Unit
) {
    val isUser = message.role == Role.USER
    val isSystem = message.role == Role.SYSTEM

    val horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = horizontalArrangement
    ) {
        if (!isUser) {
            // Mini System/Bot Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSystem) Color(0xFFFFEBEE) else darkGreenBg)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSystem) Icons.Default.Warning else Icons.Default.AutoAwesome,
                    contentDescription = "الذكاء الاصطناعي",
                    tint = if (isSystem) Color(0xFFC62828) else accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Speech Bubble Box
        Column(
            modifier = Modifier.widthIn(max = 290.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSystem -> Color(0xFFFFEBEE)
                        isUser -> darkGreenBg
                        else -> Color.White
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        isSystem -> Color(0xFFFFCDD2)
                        isUser -> darkGreenBg
                        else -> Color(0xFFE2ECE9)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCopyClick() }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // 1. Core text content
                    Text(
                        text = message.text,
                        color = when {
                            isSystem -> Color(0xFFC62828)
                            isUser -> Color.White
                            else -> darkGreenBg
                        },
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                    )

                    // 2. Optional visual image attachment
                    if (message.imageUri != null) {
                        val bmp = uriToBitmap(message.imageUri)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (bmp != null) {
                            Image(
                                bitmap = bmp,
                                contentDescription = "صورة مرفقة",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            // High-fidelity fallback design
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isUser) Color.White.copy(alpha = 0.12f)
                                        else Color(0xFFEAF4F1)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = null,
                                        tint = if (isUser) Color.White.copy(alpha = 0.5f) else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "تصميم كادر المشروع البصري 📷",
                                        fontSize = 10.sp,
                                        color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // 3. Optional visual audio play capsule
                    if (message.audioUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        var isPlaying by remember { mutableStateOf(false) }
                        var playProgress by remember { mutableStateOf(0f) }
                        
                        LaunchedEffect(isPlaying) {
                            if (isPlaying) {
                                while (playProgress < 1.0f) {
                                    delay(200)
                                    playProgress += 0.05f
                                }
                                isPlaying = false
                                playProgress = 0f
                            }
                        }

                        Surface(
                            color = if (isUser) Color.White.copy(alpha = 0.15f) else Color(0xFFF1F6F4),
                            border = BorderStroke(1.dp, if (isUser) Color.White.copy(alpha = 0.2f) else Color(0xFFE2ECE9)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "تعليق تشغيل" else "تشغيل التسجيل",
                                        tint = if (isUser) Color.White else darkGreenBg,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "مرفق صوتي 🎙️",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) Color.White else darkGreenBg
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = playProgress,
                                        color = if (isUser) BrandTiffany else darkGreenBg,
                                        trackColor = (if (isUser) Color.White else Color.Gray).copy(alpha = 0.2f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(1.5.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPlaying) "0:02" else "0:04",
                                    fontSize = 8.sp,
                                    color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            // Meta indicator below bubble to let users know they can copy
            Text(
                text = if (isUser) "أنت" else "انقر لنسخ الإجابة 📋",
                fontSize = 8.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person, 
                    contentDescription = "أنت", 
                    tint = darkGreenBg,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun ChatLoaderSteps(accentColor: Color, themeColor: Color) {
    var loadingStep by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            loadingStep = (loadingStep + 1) % 3
        }
    }

    val stepText = when (loadingStep) {
        0 -> "جاري صياغة الرد وتحليل المرفقات..."
        1 -> "تحديد اللقطات وهندسة نموذج جيميناي..."
        else -> "تحسين المخرجات عبر منصة مبسط..."
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = accentColor,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stepText,
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
