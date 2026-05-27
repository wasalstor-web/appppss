package com.example.presentation.screens.home

import android.content.Intent
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.UserSession
import com.example.presentation.components.BottomNavBar
import com.example.presentation.components.TabseetLogo
import com.example.presentation.navigation.Screen
import com.example.ui.theme.BrandTiffany
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, onNavigateToChat: (String) -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isWebSimulatorMode by remember { mutableStateOf(true) }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF07211D),
                modifier = Modifier.width(310.dp)
            ) {
                com.example.presentation.screens.dashboard.WebSidebarMenuContent(
                    activeTab = "home",
                    onTabSelect = { tab ->
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Dashboard.route)
                    },
                    onNavigateToPage = { route ->
                        scope.launch { drawerState.close() }
                        if (route != Screen.Home.route) {
                            navController.navigate(route)
                        }
                    }
                )
            }
        }
    ) {
        if (isWebSimulatorMode) {
            com.example.presentation.components.WebBrowserShell(
                navController = navController,
                currentUrl = "/home",
                pageTitle = if (UserSession.userRole == "manager") "لوحة المدير - تبسيط" else "بوابة العميل - تبسيط"
            ) {
                MainHomeScreenScaffold(
                    navController = navController,
                    scope = scope,
                    drawerState = drawerState,
                    onNavigateToChat = onNavigateToChat,
                    isWebSimulatorMode = isWebSimulatorMode,
                    onToggleWebSimulatorMode = { isWebSimulatorMode = !isWebSimulatorMode }
                )
            }
        } else {
            MainHomeScreenScaffold(
                navController = navController,
                scope = scope,
                drawerState = drawerState,
                onNavigateToChat = onNavigateToChat,
                isWebSimulatorMode = isWebSimulatorMode,
                onToggleWebSimulatorMode = { isWebSimulatorMode = !isWebSimulatorMode }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreenScaffold(
    navController: NavController,
    scope: kotlinx.coroutines.CoroutineScope,
    drawerState: DrawerState,
    onNavigateToChat: (String) -> Unit,
    isWebSimulatorMode: Boolean,
    onToggleWebSimulatorMode: () -> Unit
) {
    val context = LocalContext.current
    
    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val intent = if (isGranted) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:0504933219"))
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:0504933219"))
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    val isManager = UserSession.userRole == "manager"
    val displayName = if (UserSession.displayName.isNotBlank()) UserSession.displayName else "وصال ستور"
    val userEmail = if (UserSession.email.isNotBlank()) UserSession.email else "wasal.stor@gmail.com"

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "القائمة للهاتف",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isManager) "مرحباً يا مدير،" else "أهلاً بك يا 🌸",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick dynamic toggle for debug/switch role
                    Button(
                        onClick = {
                            UserSession.userRole = if (isManager) "customer" else "manager"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F6F4)),
                        border = BorderStroke(1.dp, BrandTiffany.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isManager) "بوابة العميل 👤" else "لوحة المدير 💼",
                            fontSize = 9.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "تنبيهات", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TabseetLogo(modifier = Modifier.width(36.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // IF LOGGED IN AS MANAGER (المدير والمشرف)
            // ==========================================
            if (isManager) {
                // Primary Admin Action
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable {
                            onNavigateToChat(UUID.randomUUID().toString())
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("محادثة جديدة بالذكاء الاصطناعي", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            Text("استشير مساعد تبسيط وباشر أتمتة الأنظمة فوراً", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pro Integrated Dashboard Card Entry
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable {
                            navController.navigate(Screen.Dashboard.route)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, BrandTiffany.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = BrandTiffany,
                                    modifier = Modifier.size(26.dp)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandTiffany.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "متابعة من الجوال 📱",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTiffany,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "منظومة التحكم والبيانات المتكاملة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "تحليل استهلاك العملاء، إحصائيات الكول سنتر والواتس CRM، وسجلات المحادثات للمشرف.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("البث المرئي التوليدي (الذكاء النشط)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))

                // Beautiful Live video card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var isPlayingFeed by remember { mutableStateOf(true) }
                        var activeAnimType by remember { mutableStateOf("wave_motion") }
                        
                        com.example.presentation.screens.dashboard.SimulatedVideoPlayerCanvas(
                            isPlaying = isPlayingFeed,
                            animationType = activeAnimType,
                            speedMultiplier = 1.2f,
                            particleDensity = 24,
                            accentColor = BrandTiffany,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE31B1B)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "wave_motion" to "موجة",
                                    "celestial_stars" to "نجوم",
                                    "cyberpunk_matrix" to "رقمي"
                                ).forEach { (style, name) ->
                                    val active = activeAnimType == style
                                    Surface(
                                        onClick = { activeAnimType = style },
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (active) BrandTiffany else Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (active) Color.White else Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { isPlayingFeed = !isPlayingFeed },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = if (isPlayingFeed) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayingFeed) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "البث الحي لمحرك التوليد المستقل",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "محاكاة فورية لأحدث أساليب الرندرة وتعديل أنماط البث المباشر الموفرة لقواعد قوقل.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("أدوات سريعة للمكتب الذكي", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))

                val tools = listOf(
                    Triple("كتابة محتوى", Icons.Default.Create, "text_generator"),
                    Triple("توليد فيديو", Icons.Default.PlayArrow, "video_ai"),
                    Triple("أتمتة العمليات", Icons.Default.Settings, "automations"),
                    Triple("الكول سنتر", Icons.Default.Call, "call_center")
                )
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tools) { tool ->
                        Box(
                            modifier = Modifier
                                .width(115.dp)
                                .height(115.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    navController.navigate(Screen.ToolDetail.createRoute(tool.third))
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(tool.second, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(tool.first, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
            // ==========================================
            // IF LOGGED IN AS CUSTOMER (العميل والمستفيد)
            // ==========================================
            else {
                // Customer Portal Hub Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF07211D)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandTiffany.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "بوابة العميل الرقمية 🌟",
                                    color = BrandTiffany,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            // Google Account badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "آمن بقواعد Google 🟢",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Text(
                            text = "مرحباً يا $displayName 🌸",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "حسابك $userEmail مرتبط حالياً بمزود قواعد البيانات السحابية لتبسيط بنسبة 100%.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Divider(color = Color.White.copy(alpha = 0.1f))

                        Spacer(modifier = Modifier.height(10.dp))

                        // Unified instant communications buttons (As customer to support from mobile)
                        Text(
                            text = "تواصل مباشر بالفريق أو الدعم الفني من الجوال:",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Dial support
                            Button(
                                onClick = {
                                    val hasCallPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.CALL_PHONE
                                    ) == PackageManager.PERMISSION_GRANTED
                                    
                                    if (hasCallPermission) {
                                        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:0504933219"))
                                        try {
                                            context.startActivity(callIntent)
                                        } catch (e: Exception) {
                                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0504933219"))
                                            try { context.startActivity(dialIntent) } catch (err: Exception) {}
                                        }
                                    } else {
                                        callPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اتصال فوري 📲", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Whatsapp CRM Direct
                            Button(
                                onClick = {
                                    val waIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://api.whatsapp.com/send?phone=966504933219&text=مرحباً بتبسيط، أرغب بدعم بخصوص الكول سنتر لمشروعي")
                                    )
                                    try {
                                        context.startActivity(waIntent)
                                    } catch (e: Exception) {
                                        // Ignore intent failure
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Green
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("محادثة واتساب 💬", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Active Customer Tickets status
                Text("حالة طلباتك الفعالة بالذكاء الاصطناعي", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF07211D))
                Spacer(modifier = Modifier.height(10.dp))

                listOf(
                    Triple("ربط رقم الكول سنتر الموحد (9200)", "قيد المعالجة بالذكاء الاصطناعي ⏱️", BrandTiffany),
                    Triple("أتمتة الرسائل الترويجية لمتجر الواتساب الخاص بك", "معلّق - انتظار تفعيل الـ Webhook ⚠️", Color(0xFFF39C12)),
                    Triple("التحقق الأمني والمصادقة المزدوجة لجافا", "مكتمل ونشط تماماً بنجاح 🟢", Color(0xFF27AE60))
                ).forEach { (title, status, tintColor) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F6F4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = null,
                                        tint = BrandTiffany,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF07211D))
                                    Text(text = "مزامنة القواعد نشطة", fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = tintColor.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tintColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive Custom Client Support AI Chat Widget!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FBF9)),
                    border = BorderStroke(1.dp, BrandTiffany.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BrandTiffany),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("المساعد التفاعلي الفوري لطلبك 🤖", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF07211D))
                                Text("اسأل الذكاء عن الأسعار والربط التلقائي بقوقل", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fast Preset questions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "كيف يتم ربط الكول سنتر؟",
                                "ما تكاليف إعداد الأتمتة؟"
                            ).forEach { q ->
                                var responseText by remember { mutableStateOf("") }
                                var promptVal by remember { mutableStateOf("") }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFE2ECE9), RoundedCornerShape(8.dp))
                                        .clickable {
                                            promptVal = q
                                            responseText = q + "\n\n" + "المستشار الذكي: أهلاً بك! ربط الكول سنتر بمستندات قوقل وتبسيط يتم آلياً عن طريق واجهة Twilio السحابية في غضون 5 دقائق فقط وقواعد البيانات مؤمنة بالكامل."
                                        }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(q, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandTiffany, textAlign = TextAlign.Center)
                                }
                            }
                        }

                        // Local Chat Text Input field
                        var customerQuery by remember { mutableStateOf("") }
                        var responseBox by remember { mutableStateOf("يرجى اختيار أحد الأسئلة السريعة بالأعلى لتجربة محاكاة التفاعل المباشر الفوري مع جيميناي السحابي...") }
                        var miniLoadingState by remember { mutableStateOf(false) }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2ECE9), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            if (miniLoadingState) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = BrandTiffany, modifier = Modifier.size(24.dp))
                                }
                            } else {
                                Text(
                                    text = responseBox,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customerQuery,
                                onValueChange = { customerQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                placeholder = { Text("اكتب استفسار مخصص هنا...", fontSize = 10.sp) },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTiffany,
                                    unfocusedBorderColor = Color(0xFFE2ECE9)
                                )
                            )
                            Button(
                                onClick = {
                                    if (customerQuery.isNotBlank()) {
                                        miniLoadingState = true
                                        scope.launch {
                                            delay(1000)
                                            responseBox = "العميل: $customerQuery\n\nالمساعد التفاعلي: تم استقبال استفسارك بخصوص الربط. يتم إرسال طلبك لخادم تبسيط وستتلقى إشعاراً فورياً على WhatsApp الخاص بك المسجل برقمك المرتبط بقوقل."
                                            customerQuery = ""
                                            miniLoadingState = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany),
                                modifier = Modifier.height(44.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
