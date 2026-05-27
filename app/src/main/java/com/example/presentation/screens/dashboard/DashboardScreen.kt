package com.example.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.BrandTiffany
import com.example.presentation.navigation.Screen
import com.example.data.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    // Premium Forest Gray theme colors locally defined for absolute fidelity
    val forestGreenBg = Color(0xFF07211D)
    val forestCardBg = Color(0xFF0B2D27)
    val lightSage = Color(0xFFEAF5F2)
    val inputSage = Color(0xFFF1F6F4)
    val lightBorder = Color(0xFFD2E4DF)
    
    // Default to 'client' for customer portals, or 'supervisor' for managers
    val defaultTab = remember {
        if (UserSession.userRole == "customer") "client" else "supervisor"
    }
    var selectedTab by remember { mutableStateOf(defaultTab) }
    var isWebSimulatorMode by remember { mutableStateOf(true) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val mainScreenContent = @Composable {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = forestGreenBg,
                    modifier = Modifier.width(310.dp)
                ) {
                    WebSidebarMenuContent(
                        activeTab = selectedTab,
                        onTabSelect = { tab ->
                            selectedTab = tab
                            scope.launch { drawerState.close() }
                        },
                        onNavigateToPage = { route ->
                            scope.launch { drawerState.close() }
                            navController.navigate(route)
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandTiffany.copy(alpha = 0.15f),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        "بوابة الويب",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTiffany,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "منصة مُبَسَّط الذكية", 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "فتح قائمة الويب", tint = Color.Black)
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
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الصفحة الرئيسية", tint = Color.Black)
                            }
                            IconButton(onClick = { /* Help */ }) {
                                Icon(Icons.Default.Info, contentDescription = "مساعدة", tint = Color.Gray)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = Color.Black
                        )
                    )
                },
                containerColor = Color(0xFFF7F9F8)
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Horizontal workspace tabs bar
                    WorkspaceMenuBar(
                        activeTab = selectedTab,
                        onTabSelect = { selectedTab = it },
                        accentColor = BrandTiffany
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Keep the active content animated for interactive aesthetic excellence
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                            },
                            label = "WorkspaceTransition"
                        ) { state ->
                            when (state) {
                                "ai_studio" -> AiStudioWorkspace(forestGreenBg, forestCardBg, BrandTiffany, inputSage, lightSage)
                                "client" -> ClientDashboardSection(BrandTiffany, forestCardBg)
                                "supervisor" -> SupervisorControlSection(BrandTiffany, forestCardBg)
                                "admin" -> AdminPortalSection(BrandTiffany, forestGreenBg)
                                "integrations" -> IntegrationsEcosystemSection(BrandTiffany, forestGreenBg, lightBorder)
                                "mcp" -> ModelContextProtocolSection(BrandTiffany, forestCardBg)
                            }
                        }
                    }
                }
            }
        }
    }

    if (isWebSimulatorMode) {
        com.example.presentation.components.WebBrowserShell(
            navController = navController,
            currentUrl = "/dashboard",
            pageTitle = "لوحة التحكم المتكاملة للمؤسسات والويب اب"
        ) {
            mainScreenContent()
        }
    } else {
        mainScreenContent()
    }
}

@Composable
fun WebSidebarMenuContent(
    activeTab: String,
    onTabSelect: (String) -> Unit,
    onNavigateToPage: (String) -> Unit
) {
    val darkMoss = Color(0xFF07211D)
    val accentColor = BrandTiffany
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkMoss)
            .padding(18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // App Header Inside Web Sidebar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp, top = 10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp),
                border = BorderStroke(1.dp, accentColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("م", color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column {
                Text(
                    text = "منصة مُبَسَّط ويب اب",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                    )
                    Text(
                        text = "الوضع السحابي المحاكي نشط",
                        fontSize = 9.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
        
        HorizontalDivider(color = Color(0xFF0F3D35), modifier = Modifier.padding(bottom = 16.dp))
        
        // Group A: Workspaces
        Text(
            text = "أقسام لوحة التحكم (Workspaces)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val workspaces = listOf(
            Triple("ai_studio", "🎨 استوديو الابتكار والفيلم", Icons.Default.Create),
            Triple("client", "💼 بوابة العميل والتحليلات", Icons.Default.Analytics),
            Triple("supervisor", "🛡️ شاشة المشرف للعمليات", Icons.Default.Assignment),
            Triple("admin", "👑 بوابة الإدارة والأرباح الكلية", Icons.Default.Lock),
            Triple("integrations", "🔌 الربط السحابي والويب", Icons.Default.Share),
            Triple("mcp", "⚙️ بروتوكول ملقم النماذج (MCP)", Icons.Default.Build)
        )
        
        workspaces.forEach { space ->
            val isSelected = activeTab == space.first
            Surface(
                onClick = { onTabSelect(space.first) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Color(0xFF0B2D27) else Color.Transparent,
                border = if (isSelected) BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = space.third,
                        contentDescription = null,
                        tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = space.second,
                        fontSize = 11.5.sp,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFF0F3D35), modifier = Modifier.padding(bottom = 16.dp))
        
        // Group B: All Web App Pages
        Text(
            text = "لوحة كل الصفحات (Web App Pages)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val pages = listOf(
            Triple("الرئيسية", Screen.Home.route, Icons.Default.Home),
            Triple("محادثات الدعم الذكي", Screen.Chat.createRoute("saas-direct-chat"), Icons.Default.Chat),
            Triple("مستودع أدوات الذكاء", Screen.Tools.route, Icons.Default.Build),
            Triple("باقات الاشتراك والدفع", Screen.Subscriptions.route, Icons.Default.CardMembership),
            Triple("سجل العمليات والتدقيق", Screen.History.route, Icons.Default.Menu),
            Triple("الملف الشخصي والعميل", Screen.Profile.route, Icons.Default.Person),
            Triple("إعدادات المنصة المتقدمة", Screen.Settings.route, Icons.Default.Settings)
        )
        
        pages.forEach { page_item ->
            Surface(
                onClick = { onNavigateToPage(page_item.second) },
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = page_item.third,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = page_item.first,
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "إصدار ويب اب مستقل v2.8.5",
            fontSize = 8.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------------------------------------
// REUSABLE SHELF & NAVIGATION MENU
// ----------------------------------------------------------------------------------
@Composable
fun WorkspaceMenuBar(
    activeTab: String,
    onTabSelect: (String) -> Unit,
    accentColor: Color
) {
    val menus = listOf(
        WorkspaceTab("ai_studio", "استوديو الابتكار", Icons.Default.Create),
        WorkspaceTab("client", "صندوق العميل", Icons.Default.Analytics),
        WorkspaceTab("supervisor", "غرفة المشرف", Icons.Default.Assignment),
        WorkspaceTab("admin", "المشرف والإدارة", Icons.Default.Lock),
        WorkspaceTab("integrations", "قنوات الربط والويب", Icons.Default.Share),
        WorkspaceTab("mcp", "منظومة MCP", Icons.Default.Build)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        menus.forEach { menu ->
            val isActive = activeTab == menu.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isActive) Color(0xFF0F3E36) else Color(0xFFF1F6F4))
                    .border(
                        1.dp,
                        if (isActive) Color(0xFF0F3E36) else Color(0xFFE2ECE9),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onTabSelect(menu.id) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = menu.icon,
                        contentDescription = menu.label,
                        tint = if (isActive) accentColor else Color(0xFF557C74),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = menu.label,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) Color.White else Color(0xFF0B2D27)
                    )
                }
            }
        }
    }
}

data class WorkspaceTab(val id: String, val label: String, val icon: ImageVector)

// ----------------------------------------------------------------------------------
// A. WORKSPACE 1: "استوديو الابتكار" (AI STUDIO - MATCHING UPLOADED SCREENS)
// ----------------------------------------------------------------------------------

data class CreatorPreset(
    val title: String,
    val prompt: String,
    val ratio: String,
    val motion: Float,
    val mode: String,
    val icon: ImageVector
)

data class CaptureData(
    val title: String,
    val tag: String,
    val prompt: String,
    val ratio: String,
    val motion: String,
    val res: String,
    val info: String,
    val time: String,
    val seed: String,
    val gradientColors: List<Color>
)

@Composable
fun LuminaEyeGraphic(accentColor: Color, modifier: Modifier = Modifier) {
    var animationTicks by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            animationTicks = (animationTicks + 1.2f) % 360f
        }
    }
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension * 0.45f
        val irisRadius = outerRadius * 0.75f
        val pupilRadius = irisRadius * 0.4f
        
        // 1. Draw glowing outer camera lens rings
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = outerRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = accentColor.copy(alpha = 0.3f),
            radius = outerRadius - 8.dp.toPx(),
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Scientific focus markings
        val markLength = 8.dp.toPx()
        for (i in 0 until 12) {
            val angle = (i * 30f + animationTicks) * (Math.PI / 180f).toFloat()
            val start = Offset(
                center.x + (outerRadius - markLength) * kotlin.math.cos(angle),
                center.y + (outerRadius - markLength) * kotlin.math.sin(angle)
            )
            val end = Offset(
                center.x + outerRadius * kotlin.math.cos(angle),
                center.y + outerRadius * kotlin.math.sin(angle)
            )
            drawLine(
                color = accentColor.copy(alpha = 0.7f),
                start = start,
                end = end,
                strokeWidth = 2.dp.toPx()
            )
        }
        
        // 2. Glowing Iris with rich radial gradient (reminiscent of the prominent emerald eye)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentColor, Color(0xFF041210)),
                center = center,
                radius = irisRadius
            ),
            radius = irisRadius,
            center = center,
            alpha = 0.9f
        )
        
        drawCircle(
            color = accentColor,
            radius = irisRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Inner iris structural lines (animated)
        for (i in 0 until 360 step 15) {
            val angle = (i + animationTicks * 0.4f) * (Math.PI / 180f).toFloat()
            val start = Offset(
                center.x + pupilRadius * 1.1f * kotlin.math.cos(angle),
                center.y + pupilRadius * 1.1f * kotlin.math.sin(angle)
            )
            val end = Offset(
                center.x + irisRadius * 0.9f * kotlin.math.cos(angle),
                center.y + irisRadius * 0.9f * kotlin.math.sin(angle)
            )
            drawLine(
                color = accentColor.copy(alpha = 0.4f),
                start = start,
                end = end,
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // 3. Central iris Aperture / Pupil
        drawCircle(
            color = Color.Black,
            radius = pupilRadius,
            center = center
        )
        // Camera lens glass glints and highlights
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = pupilRadius * 0.25f,
            center = Offset(center.x - pupilRadius * 0.35f, center.y - pupilRadius * 0.35f)
        )
        drawCircle(
            color = accentColor.copy(alpha = 0.5f),
            radius = pupilRadius * 0.12f,
            center = Offset(center.x + pupilRadius * 0.3f, center.y + pupilRadius * 0.2f)
        )
    }
}

@Composable
fun SimulatedVideoPlayerCanvas(
    isPlaying: Boolean,
    animationType: String = "wave_motion", // wave_motion, celestial_stars, cyberpunk_matrix, supernova_nebula, magnetic_atoms
    speedMultiplier: Float = 1.0f, // 0.5f to 3.0f
    particleDensity: Int = 18, // 5 to 40
    accentColor: Color = Color(0xFF0B2D27),
    modifier: Modifier = Modifier
) {
    var tick by remember { mutableStateOf(0f) }
    LaunchedEffect(isPlaying, speedMultiplier) {
        if (isPlaying) {
            while (true) {
                delay(16)
                tick += 0.05f * speedMultiplier
            }
        }
    }
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        
        // Draw deep ambient space
        drawRect(color = Color(0xFF030A09)) // Space Black
        
        when (animationType) {
            "wave_motion" -> {
                // Flowing waves of intelligence
                val numberOfWaves = 5
                for (w in 0 until numberOfWaves) {
                    val waveAlpha = 0.1f + (w * 0.12f)
                    val waveColor = accentColor.copy(alpha = waveAlpha)
                    val path = Path()
                    path.moveTo(0f, centerY)
                    for (x in 0..width.toInt() step 6) {
                        val inputAngle = (x * 0.008f) + tick + (w * 1.3f)
                        val amplitude = 22.dp.toPx() * (1f - (w * 0.12f))
                        val y = centerY + kotlin.math.sin(inputAngle) * amplitude + kotlin.math.cos(inputAngle * 0.4f) * (amplitude * 0.35f)
                        path.lineTo(x.toFloat(), y)
                    }
                    path.lineTo(width, height)
                    path.lineTo(0f, height)
                    path.close()
                    drawPath(path = path, brush = Brush.verticalGradient(colors = listOf(waveColor, Color.Transparent)))
                }
                
                // Active particles floating
                for (i in 0 until particleDensity) {
                    val pX = (width * 0.05f) + (width * 0.9f) * ((i * 13 + 7) % 13 / 13f)
                    val baseAngle = (tick + i * 1.5f)
                    val pY = centerY + kotlin.math.sin(baseAngle) * 30.dp.toPx()
                    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 2.dp.toPx(), center = Offset(pX, pY))
                    drawCircle(color = accentColor.copy(alpha = 0.25f), radius = 5.dp.toPx(), center = Offset(pX, pY))
                }
            }
            "celestial_stars" -> {
                // Moving star nebulae
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(centerX + kotlin.math.sin(tick * 0.3f) * 40.dp.toPx(), centerY + kotlin.math.cos(tick * 0.2f) * 20.dp.toPx())
                    ),
                    radius = 120.dp.toPx(),
                    center = Offset(centerX + kotlin.math.sin(tick * 0.3f) * 40.dp.toPx(), centerY + kotlin.math.cos(tick * 0.2f) * 20.dp.toPx())
                )
                
                // Shimmering stars zooming outwards
                for (i in 0 until particleDensity) {
                    val startAngle = (i * (360f / particleDensity)) * (Math.PI / 180f).toFloat()
                    val expansion = ((tick * 12f + i * 25f) % width)
                    val pX = centerX + expansion * kotlin.math.cos(startAngle)
                    val pY = centerY + expansion * kotlin.math.sin(startAngle)
                    
                    if (pX in 0f..width && pY in 0f..height) {
                        val starSize = (2.dp.toPx() + (expansion / width) * 4.dp.toPx())
                        drawCircle(color = Color.White.copy(alpha = 0.9f), radius = starSize, center = Offset(pX, pY))
                        drawLine(color = accentColor.copy(alpha = 0.5f), start = Offset(pX - 4.dp.toPx(), pY), end = Offset(pX + 4.dp.toPx(), pY), strokeWidth = 1.dp.toPx())
                        drawLine(color = accentColor.copy(alpha = 0.5f), start = Offset(pX, pY - 4.dp.toPx()), end = Offset(pX, pY + 4.dp.toPx()), strokeWidth = 1.dp.toPx())
                    }
                }
            }
            "cyberpunk_matrix" -> {
                // Matrix grid vertical cascades
                for (x in 20 until width.toInt() step 45) {
                    val drift = (tick * 110f + (x * 4)) % height
                    val length = 90.dp.toPx()
                    drawLine(
                        brush = Brush.verticalGradient(colors = listOf(Color.Transparent, accentColor.copy(alpha = 0.8f), Color.Transparent)),
                        start = Offset(x.toFloat(), drift - length),
                        end = Offset(x.toFloat(), drift),
                        strokeWidth = 2.dp.toPx()
                    )
                    
                    for (j in 0..3) {
                        val byteY = drift - (j * 16.dp.toPx())
                        if (byteY in 0f..height) {
                            val alpha = 1f - (j * 0.25f)
                            drawCircle(color = Color.White.copy(alpha = alpha), radius = 2.dp.toPx(), center = Offset(x.toFloat(), byteY))
                        }
                    }
                }
            }
            "supernova_nebula" -> {
                // Expanding rings and core explosion
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, accentColor, Color.Transparent),
                        center = Offset(centerX, centerY)
                    ),
                    radius = (20.dp.toPx() + (kotlin.math.sin(tick) * 8.dp.toPx())).coerceAtLeast(1f),
                    center = Offset(centerX, centerY)
                )
                
                // Expanding particles emitting sparks
                for (i in 0 until particleDensity) {
                    val angle = (tick * 0.4f + i * (360f / particleDensity)) * (Math.PI / 180f).toFloat()
                    val dist = (50.dp.toPx() + (kotlin.math.sin(tick * 1.5f + i) * 15.dp.toPx()))
                    val pX = centerX + dist * kotlin.math.cos(angle)
                    val pY = centerY + dist * kotlin.math.sin(angle)
                    
                    drawCircle(color = accentColor.copy(alpha = 0.9f), radius = 4.dp.toPx(), center = Offset(pX, pY))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(pX, pY))
                    
                    // Trail lines back to center
                    val tailX = centerX + (dist - 12.dp.toPx()) * kotlin.math.cos(angle)
                    val tailY = centerY + (dist - 12.dp.toPx()) * kotlin.math.sin(angle)
                    drawLine(color = accentColor.copy(alpha = 0.4f), start = Offset(pX, pY), end = Offset(tailX, tailY), strokeWidth = 2.dp.toPx())
                }
            }
            "magnetic_atoms" -> {
                // Quantum Orbits
                val orbits = 3
                for (o in 1..orbits) {
                    val radiusX = o * 40.dp.toPx()
                    val radiusY = o * 20.dp.toPx()
                    val rotationAngle = (o * 60f) * (Math.PI / 180f).toFloat()
                    
                    val path = Path()
                    for (phi in 0..360 step 10) {
                        val phiRad = phi * (Math.PI / 180f).toFloat()
                        val localX = radiusX * kotlin.math.cos(phiRad)
                        val localY = radiusY * kotlin.math.sin(phiRad)
                        
                        val rotX = centerX + localX * kotlin.math.cos(rotationAngle) - localY * kotlin.math.sin(rotationAngle)
                        val rotY = centerY + localX * kotlin.math.sin(rotationAngle) + localY * kotlin.math.cos(rotationAngle)
                        if (phi == 0) path.moveTo(rotX, rotY) else path.lineTo(rotX, rotY)
                    }
                    path.close()
                    drawPath(path = path, color = accentColor.copy(alpha = 0.25f), style = Stroke(width = 1.dp.toPx()))
                    
                    val electronPhi = (tick * 1.8f / o) * (Math.PI / 180f).toFloat()
                    val eX = centerX + radiusX * kotlin.math.cos(electronPhi) * kotlin.math.cos(rotationAngle) - radiusY * kotlin.math.sin(electronPhi) * kotlin.math.sin(rotationAngle)
                    val eY = centerY + radiusX * kotlin.math.cos(electronPhi) * kotlin.math.sin(rotationAngle) + radiusY * kotlin.math.sin(electronPhi) * kotlin.math.cos(rotationAngle)
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(eX, eY))
                    drawCircle(color = accentColor, radius = 6.dp.toPx(), center = Offset(eX, eY))
                }
            }
        }
    }
}

@Composable
fun AiStudioWorkspace(
    forestGreenBg: Color,
    forestCardBg: Color,
    accentColor: Color,
    inputSage: Color,
    lightSage: Color
) {
    val lightBorder = Color(0xFFD2E4DF)
    val scrollState = rememberScrollState()
    
    // Core parameters mapping
    var selectedMode by remember { mutableStateOf("video") } // video, image, 3d
    var promptInput by remember { mutableStateOf("Describe your creative vision in detail... e.g. 'A futuristic emerald skyscraper reflecting neon lights during a misty rain at twilight'") }
    var scaleRatio by remember { mutableStateOf("16:9") }
    var motionStrength by remember { mutableStateOf(8.5f) }
    var resolutionMode by remember { mutableStateOf("4K Ultra HD") }
    
    // NEW: Detailed video animation states
    var animationStyle by remember { mutableStateOf("wave_motion") }
    var speedMultiplier by remember { mutableStateOf(1.2f) }
    var particleCountFloat by remember { mutableStateOf(18f) }
    
    // Process States
    var isGenerating by remember { mutableStateOf(false) }
    var generationStep by remember { mutableStateOf(0) }
    var activeGeneratedAsset by remember { mutableStateOf(false) }
    var promptPresetUsed by remember { mutableStateOf("") }
    
    // Player controls
    var isPlaying by remember { mutableStateOf(true) }
    var playProgress by remember { mutableStateOf(0.45f) }
    var isLoopEnabled by remember { mutableStateOf(true) }
    
    // Overlays
    var selectedCaptureItem by remember { mutableStateOf<CaptureData?>(null) }
    var showSuccessToast by remember { mutableStateOf<String?>(null) }
    var showShareSheetAsset by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Simulated progress play runner
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                delay(100)
                playProgress = (playProgress + 0.01f)
                if (playProgress >= 1f) {
                    playProgress = if (isLoopEnabled) 0f else 1f
                    if (!isLoopEnabled) isPlaying = false
                }
            }
        }
    }

    // Preset configurations
    val presets = listOf(
        CreatorPreset(
            title = "زمرد نيون معماري", 
            prompt = "A high-end minimal pavilion made of curved glowing emerald glass reflecting pristine water, structural architecture render, cinematically lit atmosphere, 8K resolution.", 
            ratio = "16:9", 
            motion = 8.5f, 
            mode = "image", 
            icon = Icons.Default.Home
        ),
        CreatorPreset(
            title = "غابة المستقبل الرقمية", 
            prompt = "Hyper-detailed camera pan over a cyberpunk deep neon jungle, misty forest pathways, emerald moss emitting soft organic cyan light, cinematic masterpiece.", 
            ratio = "9:16", 
            motion = 9.2f, 
            mode = "video", 
            icon = Icons.Default.Settings
        ),
        CreatorPreset(
            title = "بلورات ميكرو متألقة", 
            prompt = "Micro-photography render of floating organic crystal geometric stones, green sapphire prisms catching beams of white light, photorealistic 3D sculpture depth.", 
            ratio = "1:1", 
            motion = 4.0f, 
            mode = "3d", 
            icon = Icons.Default.Brush
        ),
        CreatorPreset(
            title = "لوحة لومينا بورتريه", 
            prompt = "Studio close-up portrait of a solar-punk tech model, glowing organic skin decals, soft golden-hour backlight with emerald highlights, award-winning lighting.", 
            ratio = "4:3", 
            motion = 2.5f, 
            mode = "image", 
            icon = Icons.Default.Face
        )
    )

    val recentCaptures = listOf(
        CaptureData(
            title = "Minimalist Glass Pavilion",
            tag = "ثنائي الأبعاد / 4K READY",
            prompt = "A high-end minimal pavilion made of curved glowing emerald glass reflecting pristine water, structural architecture render, cinematically lit atmosphere, 8K resolution.",
            ratio = "16:9",
            motion = "8.2 / 10",
            res = "4K Ultra HD",
            info = "تم التوليد بنظام Lumina-8 الفائق - إصدار الإصلاح v2",
            time = "دقيقتين مضت",
            seed = "830294719",
            gradientColors = listOf(Color(0xFF0F3E36), Color.Black)
        ),
        CaptureData(
            title = "Fluid Emerald Textures",
            tag = "مقطع فيديو / Lumina",
            prompt = "Cosmic fluid simulation, emerald and gold particles swirling, abstract dark background, premium 3D sculpture, smooth wave flow",
            ratio = "1:1",
            motion = "9.5 / 10",
            res = "1080p FHD",
            info = "محرك الحركة السينمائي المتقدم نشط",
            time = "12 دقيقة مضت",
            seed = "918237462",
            gradientColors = listOf(Color(0xFF145247), Color.Black)
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Platform Elegant Header
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = "استوديو الرسوم والإنتاج الاحترافي",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = forestGreenBg,
                        textAlign = TextAlign.Right
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "منفذ المعالجة الرسومية الأقوى لنماذج جيميناي ولومينا الإبداعية. صمم أصولاً معمارية ومقاطع سينمائية فائقة الدقة بلمسة زر واحدة.",
                    fontSize = 11.sp,
                    color = Color(0xFF557C74),
                    lineHeight = 16.sp
                )
            }

            // A. Creative Mode Selection & Main Workspace
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color(0xFFE5EDE9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                            Text("وضع الابتكار (Creation Mode)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = forestGreenBg)
                        }
                        
                        // Capsule tabs (video, image, 3d)
                        Row(
                            modifier = Modifier
                                .background(inputSage, RoundedCornerShape(10.dp))
                                .padding(3.dp)
                        ) {
                            listOf(
                                Pair("video", "فيديو"),
                                Pair("image", "صور"),
                                Pair("3d", "ثلاثي الأبعاد")
                            ).forEach { mode ->
                                val isSelected = selectedMode == mode.first
                                Text(
                                    text = mode.second,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color.White else Color.Transparent)
                                        .clickable { selectedMode = mode.first }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = if (isSelected) forestGreenBg else Color(0xFF557C74)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multiline Creative Prompt text Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(inputSage)
                            .border(1.dp, lightBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            BasicTextField(
                                value = promptInput,
                                onValueChange = { promptInput = it },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = forestGreenBg,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Enhance prompt button
                                TextButton(
                                    onClick = {
                                        promptInput = "A cinematic masterclass render of architectural majesty, $promptInput, photorealistic, cinematic camera lenses, perfect reflections."
                                        showSuccessToast = "✨ تم تحسين وصياغة الرد تكنولوجيا بفلاتر لومينا السينمائية"
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.Face, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تحسين فوري للطلب", color = forestGreenBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                // Main generate button
                                Button(
                                    onClick = {
                                        if (isGenerating) return@Button
                                        isGenerating = true
                                        generationStep = 0
                                        activeGeneratedAsset = false
                                        scope.launch {
                                            delay(800)
                                            generationStep = 1
                                            delay(800)
                                            generationStep = 2
                                            delay(800)
                                            generationStep = 3
                                            delay(600)
                                            isGenerating = false
                                            activeGeneratedAsset = true
                                            showSuccessToast = "🎨 تم إنتاج وتصميم الأصل السينمائي بدقة 4K بنجاح"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = forestGreenBg, contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                                ) {
                                    Text("توليد المحتوى السينمائي", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // Horizontal Quick-set Preset Prompts (Matching professional workflows)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💡 قوالب مستوحاة لتوليد سريع ومحترف:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = forestGreenBg,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { preset ->
                            val isPresetCurrentlyActive = promptInput == preset.prompt
                            Card(
                                modifier = Modifier
                                    .clickable {
                                        promptInput = preset.prompt
                                        scaleRatio = preset.ratio
                                        motionStrength = preset.motion
                                        selectedMode = preset.mode
                                        promptPresetUsed = preset.title
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPresetCurrentlyActive) Color(0xFFEAF5F2) else inputSage
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isPresetCurrentlyActive) accentColor else lightBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(preset.icon, contentDescription = null, tint = forestGreenBg, modifier = Modifier.size(12.dp))
                                    Text(preset.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                                }
                            }
                        }
                    }
                }
            }

            // B. Simulated High-Tech Generation Stepper Logs
            AnimatedVisibility(
                visible = isGenerating,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = forestCardBg),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                color = accentColor,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                "اتصال المعالج السحابي بمسرعات الرسوم مبسط...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Beautiful linear steps representation
                        val stepsList = listOf(
                            "تهيؤ الخادوم المسرع وبث مصفوفات جيميناي",
                            "تحليل الطلب الإبداعي وتعميم الترددات الهيكلية",
                            "إكساء الرسوم المعمارية وتنعيم الظلال وحجم الإفراط",
                            "تصدير دقة لومينا 4K فائقة الجودة وعقد الألوان الموحدة"
                        )
                        
                        stepsList.forEachIndexed { index, titleText ->
                            val isActiveStep = index == generationStep
                            val isCompletedStep = index < generationStep
                            val opacity = if (isActiveStep || isCompletedStep) 1f else 0.4f
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCompletedStep -> accentColor
                                                isActiveStep -> Color.White
                                                else -> Color.Gray
                                            }
                                        )
                                )
                                Text(
                                    text = titleText,
                                    color = if (isActiveStep) accentColor else Color.White.copy(alpha = opacity),
                                    fontSize = 11.sp,
                                    fontWeight = if (isActiveStep) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // C. Stunning Masterpiece Generated Asset Preview Board
            AnimatedVisibility(
                visible = activeGeneratedAsset,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, accentColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column {
                        // Dynamic Animated Canvas Preview Window representing generated content
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        ) {
                            SimulatedVideoPlayerCanvas(
                                isPlaying = isPlaying,
                                animationType = animationStyle,
                                speedMultiplier = speedMultiplier,
                                particleDensity = particleCountFloat.toInt(),
                                accentColor = accentColor,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Top overlay details
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF25D366)))
                                        Text("معاينة سحابية حية", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    color = accentColor,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (selectedMode == "video") "فيديو سينمائي" else "رسم ثلاثي الأبعاد",
                                        color = forestGreenBg,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            
                            // Bottom seek bar overlay
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { isPlaying = !isPlaying },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    
                                    // Custom beautifully designed seeker slider
                                    Slider(
                                        value = playProgress,
                                        onValueChange = { playProgress = it },
                                        colors = SliderDefaults.colors(
                                            thumbColor = accentColor,
                                            activeTrackColor = accentColor,
                                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                            .height(18.dp)
                                    )

                                    Text(
                                        text = "0:${String.format("%02d", (playProgress * 12).toInt())} / 0:12",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Info Metadata details below preview
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("الأصل المُفرَز: Lumina_Suite_Output_4K.mp4", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 2.dp)) {
                                        Text("الدقة: $resolutionMode", fontSize = 9.sp, color = Color.Gray)
                                        Text("الأبعاد: $scaleRatio", fontSize = 9.sp, color = Color.Gray)
                                        Text("قوة الحركة: $motionStrength", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = inputSage)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom sharing suite (matching client's whatsapp desires)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        showSuccessToast = "💾 تم حفظ الفيديو بنجاح في استوديو العميل بالأجهزة"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = forestGreenBg),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تصدير وحفظ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        showShareSheetAsset = promptInput
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشاركة وتكامل WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // D. Precision Tuning Parameter Panel
            Text("ضبط فلاتر التفصيل (Control Parameters)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color(0xFFE5EDE9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Aspect ratio selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("نسبة العرض للارتفاع", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                            Text("Aspect Ratio", fontSize = 10.sp, color = Color.Gray)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("16:9", "9:16", "1:1", "4:3").forEach { ratio ->
                                val isActive = scaleRatio == ratio
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isActive) forestGreenBg else Color.Transparent)
                                        .border(1.dp, if (isActive) forestGreenBg else lightBorder, RoundedCornerShape(8.dp))
                                        .clickable { scaleRatio = ratio }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ratio,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) accentColor else forestGreenBg
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = inputSage)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Quality resolution selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("دقة مخرجات الأصول", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                            Text("Output Resolution", fontSize = 10.sp, color = Color.Gray)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("4K Ultra HD", "1080p FHD").forEach { res ->
                                val isActive = resolutionMode == res
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isActive) Color(0xFFEAF5F2) else Color.Transparent)
                                        .border(1.dp, if (isActive) accentColor else lightBorder, RoundedCornerShape(8.dp))
                                        .clickable { resolutionMode = res }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isActive) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(accentColor)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = res,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = forestGreenBg
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = inputSage)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Motion Strength Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("قوة انسيابية الحركة التوليدية (Motion)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                            Text("$motionStrength / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = motionStrength,
                            onValueChange = { motionStrength = kotlin.math.round(it * 10f) / 10f },
                            valueRange = 1f..10f,
                            colors = SliderDefaults.colors(
                                thumbColor = forestGreenBg,
                                activeTrackColor = accentColor,
                                inactiveTrackColor = inputSage
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = inputSage)
                    Spacer(modifier = Modifier.height(14.dp))

                    // NEW: Professional Animation Style Selector
                    Column {
                        Text("نمط الحركة السينمائية الفنية", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                        Text("Interactive Animation Style", fontSize = 9.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("wave_motion", "موج ذكي", Icons.Default.Waves),
                                Triple("celestial_stars", "فضاء لومينا", Icons.Default.Star),
                                Triple("cyberpunk_matrix", "رمز رقمي", Icons.Default.Code),
                                Triple("supernova_nebula", "سوبرنوفا", Icons.Default.Stream),
                                Triple("magnetic_atoms", "ذرات الجاذبية", Icons.Default.Grain)
                            ).forEach { item ->
                                val isActive = animationStyle == item.first
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isActive) forestGreenBg else Color.Transparent)
                                        .border(1.dp, if (isActive) forestGreenBg else lightBorder, RoundedCornerShape(8.dp))
                                        .clickable { animationStyle = item.first }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(item.third, null, tint = if (isActive) accentColor else forestGreenBg, modifier = Modifier.size(11.dp))
                                        Text(
                                            text = item.second,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) accentColor else forestGreenBg
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = inputSage)
                    Spacer(modifier = Modifier.height(14.dp))

                    // NEW: Real-time Speed Multiplier Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("سرعة إطارات العرض (Render Velocity)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                                Text("Calculates wave frequencies in real time", fontSize = 9.sp, color = Color.Gray)
                            }
                            Text("${String.format("%.1f", speedMultiplier)}x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = speedMultiplier,
                            onValueChange = { speedMultiplier = it },
                            valueRange = 0.5f..3.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = forestGreenBg,
                                activeTrackColor = accentColor,
                                inactiveTrackColor = inputSage
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = inputSage)
                    Spacer(modifier = Modifier.height(14.dp))

                    // NEW: Particle Density Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("كثافة الألياف والجسيمات (Particle Density)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = forestGreenBg)
                                Text("Number of physics emitters drawn in canvas", fontSize = 9.sp, color = Color.Gray)
                            }
                            Text("${particleCountFloat.toInt()} ذرة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = particleCountFloat,
                            onValueChange = { particleCountFloat = it },
                            valueRange = 5f..40f,
                            colors = SliderDefaults.colors(
                                thumbColor = forestGreenBg,
                                activeTrackColor = accentColor,
                                inactiveTrackColor = inputSage
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // E. Immersive "Lumina-8 Cinema Suite" Promo Card (Using interactive Glowing Eye graphic)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = forestCardBg)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = accentColor.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text("محرك معزز نشط: لومينا الإصدار الثامن", color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Text(
                            text = "Lumina-8 Cinema Suite",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نموذج الذكاء الإصطناعي الرسومي الأكثر تطوراً. دقة متناهية ومعالجة حافات الإضاءة بأسلوب معمارى وهندسي رائع.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                promptInput = "Cinematic shot from extreme aperture lens, Lumina-8 high-fidelity architecture pavilion, glowing green light trails, award-winning photography."
                                showSuccessToast = "🔬 تم تفعيل إعدادات لومينا السينمائية الفائقة بنجاح"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF07211D)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("تطبيق معايير Lumina-8", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // High-tech Eye iris scanner lens graphic (Canvas inspired by uploaded screen)
                    LuminaEyeGraphic(
                        accentColor = accentColor,
                        modifier = Modifier
                            .size(110.dp)
                            .weight(0.7f)
                    )
                }
            }

            // F. Recent Captures Visual Gallery with click-to-lightbox capability
            Text("أحدث الأصول التوليدية (Recent Captures)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                recentCaptures.forEach { capture ->
                    CaptureCardItem(
                        title = capture.title,
                        tag = capture.tag,
                        bgGradient = Brush.verticalGradient(colors = capture.gradientColors),
                        onClick = {
                            selectedCaptureItem = capture
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ----------------- OVERLAYS AND TOAST INTERATIONS -----------------

        // 1. Image / Video Lightbox detailed overlay dialogue (Slick bottom overlay)
        AnimatedVisibility(
            visible = selectedCaptureItem != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            val capture = selectedCaptureItem
            if (capture != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .clickable { selectedCaptureItem = null },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp)
                            .clickable(enabled = false) { }, // consume click prevent dismiss
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, accentColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = accentColor)
                                    Text("تفاصيل الأصل التوليدي المهيأ", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = forestGreenBg)
                                }
                                IconButton(onClick = { selectedCaptureItem = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = forestGreenBg)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Beautiful abstract representation box of the asset
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.verticalGradient(colors = capture.gradientColors)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize().alpha(0.12f)) {
                                    drawCircle(color = accentColor, radius = size.minDimension / 2f)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = accentColor, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(capture.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(capture.tag, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Technical prompt detail
                            Text("الأمر البرمجي المستعمل (Prompt):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = forestGreenBg)
                            Surface(
                                color = inputSage,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, lightBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = capture.prompt,
                                    color = forestGreenBg,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Parameters Grid representation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Pair("الأبعاد", capture.ratio),
                                    Pair("الحركة", capture.motion),
                                    Pair("الدقة", capture.res),
                                    Pair("رقاقة التدفق", "Seed: " + capture.seed.take(5))
                                ).forEach { param ->
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = inputSage),
                                        border = BorderStroke(1.dp, lightBorder)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(param.first, fontSize = 9.sp, color = Color.Gray)
                                            Text(param.second, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = forestGreenBg, textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        promptInput = capture.prompt
                                        scaleRatio = capture.ratio
                                        selectedCaptureItem = null
                                        showSuccessToast = "📋 تم نسخ واستيراد الأمر التوليدي إلى صندوق الابتكار بنجاح"
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = forestGreenBg),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("أعد تكرار الأمر", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        showShareSheetAsset = capture.prompt
                                    },
                                    modifier = Modifier.weight(1.3f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مشاركة WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Custom Success Toast banner (Slick HUD style)
        AnimatedVisibility(
            visible = showSuccessToast != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -100 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -100 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            val message = showSuccessToast
            if (message != null) {
                Surface(
                    color = forestGreenBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, accentColor),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "نجاح", tint = accentColor)
                        Text(
                            text = message,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                LaunchedEffect(showSuccessToast) {
                    delay(3000)
                    showSuccessToast = null
                }
            }
        }

        // 3. Simulated direct share / WhatsApp integration dialog
        AnimatedVisibility(
            visible = showShareSheetAsset != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            val promptText = showShareSheetAsset
            if (promptText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { showShareSheetAsset = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clickable(enabled = false) { },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, Color(0xFF25D366))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(26.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                "تكامل WhatsApp المباشر المسرّع",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = forestGreenBg,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                "هل ترغب في تصدير أصل وسائط مصفوفة لومينا السينمائية وتفاصيل الرموز البرمجية مباشرة إلى جهة الاتصال أو للمجموعات النشطة؟",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Mock preview message bubble
                            Surface(
                                color = Color(0xFFE2F0EC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("رابط التوليد المباشر لجيميناي ومبسط:", fontSize = 8.sp, color = Color(0xFF557C74))
                                    Text("🤖 تم إنتاج أصل سينمائي فائق الدقة 4K بنجاح عبر استوديو الرسوم لشركة لومينا.\nالأمر المستعمل:\n\"${promptText.take(45)}...\"", fontSize = 9.sp, color = forestGreenBg, fontWeight = FontWeight.Medium)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(18.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showShareSheetAsset = null },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("إلغاء", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }

                                Button(
                                    onClick = {
                                        showShareSheetAsset = null
                                        showSuccessToast = "🟢 تم الإرسال ومشاركة الرابط على WhatsApp بنجاح!"
                                    },
                                    modifier = Modifier.weight(1.3f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("إرسال الآن للعميل", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaptureCardItem(
    title: String,
    tag: String,
    bgGradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(145.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFD2E4DF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(14.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            // Draw visual abstract aesthetic elements inside cards
            Canvas(modifier = Modifier
                .fillMaxSize()
                .alpha(0.12f)) {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx()
                )
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension * 0.35f,
                    center = Offset(size.width * 0.8f, size.height * 0.2f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            Column {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandTiffany.copy(alpha = 0.25f),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = tag, 
                        color = BrandTiffany, 
                        fontSize = 8.sp, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = title, 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 11.sp
                )
                Text(
                    text = "انقر لعرض وتصدير التفاصيل 🔍", 
                    color = Color.White.copy(alpha = 0.6f), 
                    fontSize = 7.5.sp, 
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// B. WORKSPACE 2: "صندوق العميل" (CLIENT BOX - BALANCES & STATS)
// ----------------------------------------------------------------------------------
@Composable
fun ClientDashboardSection(
    accentColor: Color,
    forestCardBg: Color
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ميزانية الاستهلاك المتبقية للعميل", fontSize = 11.sp, color = Color.Gray)
                        Text("432.84 $", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = forestCardBg)
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = forestCardBg, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F6F4))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("معدل الكلمات المستهلكة (Pro Plan)", fontSize = 10.sp, color = Color.Gray)
                    Text("31,241 / 50,000 كلمة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 31241f / 50000f },
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
            }
        }

        Text("معدلات الاستعلام والأنشطة الرقمية", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestCardBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MetricLineChart(
                    dataPoints = listOf(14f, 25f, 18f, 32f, 40f, 22f, 45f, 38f, 52f, 41f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("الحد الأدنى: 14 طلبًا", fontSize = 9.sp, color = Color.Gray)
                    Text("ذروة المساء: 52 طلبًا", fontSize = 9.sp, color = forestCardBg, fontWeight = FontWeight.Bold)
                    Text("المجموع: 297 طلبًا", fontSize = 9.sp, color = Color.Gray)
                }
            }
        }
        
        // Quick statistics labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCapBox("الأتمتات النشطة", "5 عمليات متزامنة", accentColor, modifier = Modifier.weight(1f))
            StatCapBox("زمن الاستجابة للويب", "1.1 ثانية كحد أقصى", Color(0xFF4CB396), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCapBox(label: String, valStr: String, barCol: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(valStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF07211D))
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(barCol)
            )
        }
    }
}

// ----------------------------------------------------------------------------------
// C. WORKSPACE 3: "غرفة المشرف" (SUPERVISOR AUDITING & VERIFICATIONS)
// ----------------------------------------------------------------------------------
@Composable
fun SupervisorControlSection(
    accentColor: Color,
    forestCardBg: Color
) {
    val scrollState = rememberScrollState()
    var autoFiltersEnabled by remember { mutableStateOf(true) }
    var auditList = remember {
        mutableStateListOf(
            SupervisorAuditItem("مراجعة مخرجات بريدية مريبة", "مبيعات #281 - ثقة 71%", "إجراء فوري", 1),
            SupervisorAuditItem("طلب إذن أتمتة حذف متعدد", "عميل متميز #402 - مالي", "هام جداً", 2)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // glowing status panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = forestCardBg)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("المراقبة الإشرافية والاتصال السحابي آمنون بنسبة 99.98%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Text("مؤشرات الفلترة والتحقيق البشري", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestCardBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MetricBarChart(
                    dataValues = listOf(98f, 92f, 85f, 95f),
                    labels = listOf("الاتصالات", "الاستجابة", "دقة الفلاتر", "سعة القنوات"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    barColor = accentColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("الاتصال", fontSize = 9.sp, color = Color.Gray)
                    Text("الاستجابة", fontSize = 9.sp, color = Color.Gray)
                    Text("الفلاتر", fontSize = 9.sp, color = Color.Gray)
                    Text("الشبكة", fontSize = 9.sp, color = Color.Gray)
                }
            }
        }

        // Auto filters switch
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("فرز الأخطاء والمراجعات التلقائي", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("يقلل من المراجعة البشرية بنسبة 85%", fontSize = 10.sp, color = Color.Gray)
                }
                Switch(
                    checked = autoFiltersEnabled,
                    onCheckedChange = { autoFiltersEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accentColor
                    )
                )
            }
        }

        // Pending audits with actions
        Text("طابور الفرز والتدقيق المعلق", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestCardBg, modifier = Modifier.padding(bottom = 8.dp))
        if (auditList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد ملفات تحتاج لتدقيق بشري حالياً. رائع!", color = Color.Gray, fontSize = 11.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                auditList.forEach { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Surface(
                                    color = Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(4.dp)
                               ) {
                                    Text(task.badge, color = Color(0xFFC62828), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(task.desc, fontSize = 10.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { auditList.remove(task) }) {
                                    Text("رفض الطلب وبلوك", color = Color(0xFFC62828), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = { auditList.remove(task) },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = forestCardBg),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text("موافقة وتمرير", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SupervisorAuditItem(val title: String, val desc: String, val badge: String, val id: Int)

// ----------------------------------------------------------------------------------
// D. WORKSPACE 4: "لوحة الإدارة" (ADMIN SEATS, LIMITS & RATES)
// ----------------------------------------------------------------------------------
@Composable
fun AdminPortalSection(
    accentColor: Color,
    forestGreenBg: Color
) {
    val scrollState = rememberScrollState()
    var testServerMocking by remember { mutableStateOf(false) }
    var enforceUsageCap by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("تراخيص وأعضاء لوحة القيادة", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("المقاعد الإدارية المشغولة حالياً", fontSize = 10.sp, color = Color.Gray)
                    Text("8 / 12 ترخيص نشط", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 8f / 12f },
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F6F4), contentColor = forestGreenBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = forestGreenBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("شراء مقاعد إضافية للفريق", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Budgeting control breakdown
        Text("تكلفة استهلاك البنى السحابية", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AdminCostRow("تكاليف معالجة غوغل جيميناي ونماذج سياق الموديل", "132.40 $", accentColor)
                HorizontalDivider(color = Color(0xFFF1F6F4), modifier = Modifier.padding(vertical = 10.dp))
                AdminCostRow("ربط بوابة الرسائل والميكروفونات التلقائية", "42.10 $", Color(0xFF3B9B82))
            }
        }

        // Strict system safety switches
        Text("إعدادات سلامة الاستهلاك والاستعلام", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("السماح بمحاكاة السيرفر دون رصيد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("وضع مفيد للاختبار والتطوير", fontSize = 9.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = testServerMocking,
                        onCheckedChange = { testServerMocking = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentColor
                        )
                    )
               }
               HorizontalDivider(color = Color(0xFFF1F6F4), modifier = Modifier.padding(vertical = 10.dp))
               Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("تحديد الاستهلاك الأقصى لكل ساعة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("يمنع تسريبات الداتا والرموز والاستعلام العشوائي", fontSize = 9.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = enforceUsageCap,
                        onCheckedChange = { enforceUsageCap = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentColor
                        )
                    )
               }
            }
        }
    }
}

@Composable
fun AdminCostRow(label: String, cost: String, tagColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(tagColor))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 11.sp, color = Color.DarkGray)
        }
        Text(cost, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

// ----------------------------------------------------------------------------------
// E. WORKSPACE 5: "قنوات الربط" (INTEGRATIONS ECOSYSTEM - WEBHOOKS)
// ----------------------------------------------------------------------------------
@Composable
fun IntegrationsEcosystemSection(
    accentColor: Color,
    forestGreenBg: Color,
    lightBorder: Color
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Visual Node Transfer Flow representation
        Text("خريطة التوصيلات السحابية المباشرة (Core Flow)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IntegrationNode("WhatsApp AI", Color(0xFF25D366))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    IntegrationNode("Mubassat LLM", forestGreenBg)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    IntegrationNode("Slack Hook", Color(0xFF4A154B))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text("معدل الحركة: 1,421 استدعاء webhook بنجاح (100% جهوزية)", color = Color(0xFF358572), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Webhook receiver address
        Text("رابط استقبال المكالمات والأحداث الفورية", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F6F4))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "https://io.tabseet.ai/v1/wh_9x0lk2",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("https://io.tabseet.ai/v1/wh_9x0lk2"))
                            isCopied = true
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy Hook",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("انسخ هذا النطاق السحابي متبوعاً بحقول الـ JSON للربط مع زابيير، دروبوكس أو سيرفرات مبيعاتك الشخصية بالتبادل.", fontSize = 9.sp, color = Color.Gray, lineHeight = 13.sp)
            }
        }

        // Error latency bars
        Text("معدل الكفاءة والاستقرار للقنوات", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestGreenBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                listOf(
                    Pair("WhatsApp Business Hub API", 0.99f),
                    Pair("Slack Telemetry Integrator", 0.97f),
                    Pair("Zapier Instant Event Hook", 0.89f)
                ).forEachIndexed { i, item ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.first, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${(item.second * 100).toInt()}% نجاح الاتصال", fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { item.second },
                            color = accentColor,
                            trackColor = Color(0xFFF1F6F4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )
                    }
                    if (i < 2) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun IntegrationNode(name: String, col: Color) {
    Box(
        modifier = Modifier
            .width(85.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(col.copy(alpha = 0.12f))
            .border(1.dp, col, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(name, color = col, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

// ----------------------------------------------------------------------------------
// F. WORKSPACE 6: "بروتوكول MCP" (MODEL CONTEXT PROTOCOL - DYNAMIC FLOW CLI SIMULATOR)
// ----------------------------------------------------------------------------------
@Composable
fun ModelContextProtocolSection(
    accentColor: Color,
    forestCardBg: Color
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var appMcpCount by remember { mutableStateOf(3) }
    var pingValue by remember { mutableStateOf(145) }
    
    // Live Trace console elements
    val traceLogs = remember {
        mutableStateListOf(
            "SYS: [Mubassat MCP v1.8] تم تشغيل الخادم والبروتوكول المحلي...",
            "DB_READ: محاذاة مقابس قاعدة بيانات زبائن Postgres بنجاح.",
            "MCP_CLIENT: جاري التنصت على منفذ الاتصالات المتزامنة 5001..."
        )
    }
    var isRunningTrace by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Banner Explanations
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = forestCardBg)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("ما هو بروتوكول MCP السحابي؟", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "بروتوكول سياق النموذج (Model Context Protocol) يربط النماذج الذكية بأدوات الحوسبة وقواعد البيانات والملفات والمحركات الخارجية بأمان فائق، دون الحاجة لبوابات وسيطة معقدة.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }

        // Ping status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCapBox("خوادم MCP النشطة", "$appMcpCount سيرفرات مستقرة", accentColor, modifier = Modifier.weight(1f))
            StatCapBox("سرعة الاستدعاء للبروتوكول", "$pingValue ميلي ثانية", Color(0xFFF0AD4E), modifier = Modifier.weight(1f))
        }

        // Schema tools listing
        Text("الأدوات وقواعد السياق المسجلة للموديل (Registered Tools)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestCardBg, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                McpToolRow("sql_sales_reader", "تنفيذ عبارات البحث في جداول الموديلات وقراءة المبيعات المالية", "PostgreSQL Web")
                HorizontalDivider(color = Color(0xFFF1F6F4), modifier = Modifier.padding(vertical = 8.dp))
                McpToolRow("local_directory_agent", "فحص وجلب الملفات، لوحات البيانات والمعايير على استضافة تبسيط", "File System")
                HorizontalDivider(color = Color(0xFFF1F6F4), modifier = Modifier.padding(vertical = 8.dp))
                McpToolRow("unified_live_feed", "تحديث فوري لمعايير العملات والذهب من بوابات إلكترونية معتمدة", "API Portal")
            }
        }

        // Console logger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("مراقب معالجة النداء المباشر (Live MCP CLI Tracker)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = forestCardBg)
            
            TextButton(
                onClick = {
                    if (isRunningTrace) return@TextButton
                    isRunningTrace = true
                    scope.launch {
                        traceLogs.add("> USER_CALL: استدعاء أداة تفقد العائدات sql_sales_reader...")
                        delay(700)
                        traceLogs.add("> MCP_SERVER: التحقق من صلاحيات العميل واسترجاع 4 جداول بنجاح.")
                        delay(600)
                        traceLogs.add("> LLM_RESPONSE: الموديل يفسر الداتا ويولد تقرير مبيعات فوري.")
                        isRunningTrace = false
                    }
                }
            ) {
                if (isRunningTrace) {
                    CircularProgressIndicator(color = accentColor, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("محاكاة اتصال وسياق أدوات", color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F211E))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(traceLogs.toList()) { log ->
                    Text(
                        text = log,
                        color = if (log.startsWith("> USER_CALL")) accentColor else Color.White.copy(alpha = 0.8f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun McpToolRow(name: String, desc: String, src: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(BrandTiffany)
                .padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
            Text(desc, fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
        }
        Surface(
            color = Color(0xFFF1F6F4),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(src, color = Color(0xFF456D64), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
    }
}

// ----------------------------------------------------------------------------------
// OTHER REUSABLE CUSTOM GRAPH DRAWINGS
// ----------------------------------------------------------------------------------
@Composable
fun MetricLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = BrandTiffany
) {
    Canvas(modifier = modifier) {
        if (dataPoints.isEmpty()) return@Canvas
        val width = size.width
        val height = size.height
        val maxVal = dataPoints.maxOrNull() ?: 1f
        val minVal = dataPoints.minOrNull() ?: 0f
        val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

        val points = dataPoints.mapIndexed { i, value ->
            val x = (i.toFloat() / (dataPoints.size - 1)) * width
            val y = height - ((value - minVal) / range) * height * 0.75f - (height * 0.12f)
            Offset(x, y)
        }

        // Grid lines
        for (g in 0..4) {
            val hY = (g.toFloat() / 4) * height
            drawLine(
                color = lineColor.copy(alpha = 0.08f),
                start = Offset(0f, hY),
                end = Offset(width, hY),
                strokeWidth = 1f
            )
        }

        // smooth curve bezier path
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val pPrev = points[i - 1]
                    val pCurr = points[i]
                    val controlX = (pPrev.x + pCurr.x) / 2
                    cubicTo(controlX, pPrev.y, controlX, pCurr.y, pCurr.x, pCurr.y)
                }
            }
        }

        // shader brush under path
        val fillPath = Path()
        if (points.isNotEmpty()) {
            fillPath.addPath(path)
            fillPath.lineTo(points.last().x, height)
            fillPath.lineTo(points.first().x, height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.22f), Color.Transparent)
                )
            )
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw node circles
        points.forEach { p ->
            drawCircle(color = lineColor, radius = 5.dp.toPx(), center = p)
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = p)
        }
    }
}

@Composable
fun MetricBarChart(
    dataValues: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = BrandTiffany
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxVal = dataValues.maxOrNull() ?: 1f
        val barCount = dataValues.size
        val gap = width / (barCount * 3)
        val barWidth = (width - gap * (barCount + 1)) / barCount

        dataValues.forEachIndexed { i, value ->
            val barHeight = (value / maxVal) * height * 0.82f
            val left = gap + i * (barWidth + gap)
            val top = height - barHeight
            val right = left + barWidth
            val bottom = height

            val barPath = Path().apply {
                addRoundRect(
                    roundRect = RoundRect(
                        left = left,
                        top = top,
                        right = right,
                        bottom = bottom,
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
               )
            }

            drawPath(
                path = barPath,
                brush = Brush.verticalGradient(
                    colors = listOf(barColor, barColor.copy(alpha = 0.45f))
               )
            )
        }
    }
}
