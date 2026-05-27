package com.example.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.presentation.navigation.Screen
import com.example.ui.theme.BrandTiffany
import kotlinx.coroutines.delay

@Composable
fun WebBrowserShell(
    navController: NavController,
    currentUrl: String,
    pageTitle: String,
    showSidebar: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    // We can define premium web app background colors
    val browserChromeBg = Color(0xFF1E2624)
    val addressBarBg = Color(0xFF111716)
    val webBg = Color(0xFFF4F7F6)

    var isReloading by remember { mutableStateOf(false) }

    LaunchedEffect(isReloading) {
        if (isReloading) {
            delay(1200)
            isReloading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Web Browser Top Chrome Layout ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = browserChromeBg,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. Browser Window Control Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                        Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                        Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Active Browser Mode badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BrandTiffany.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, BrandTiffany.copy(alpha = 0.5f))
                        ) {
                            Text(
                                "تحاكي Web App نشط",
                                color = BrandTiffany,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // 2. Navigation Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { /* Forward navigation */ },
                            modifier = Modifier.size(28.dp),
                            enabled = false
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { isReloading = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reload",
                                tint = if (isReloading) BrandTiffany else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // 3. User & Quick Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FFCC))
                        )
                        Text(
                            text = "https secure",
                            fontSize = 10.sp,
                            color = BrandTiffany,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // --- URL / Address Bar & Page Tab ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // URL input box (displaying mockup website URL)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = addressBarBg,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Secure Connection",
                                tint = BrandTiffany,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "https://mobasit.ai_studio$currentUrl",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                            if (isReloading) {
                                CircularProgressIndicator(
                                    color = BrandTiffany,
                                    strokeWidth = 1.5.dp,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Tab name indicator
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "$pageTitle | مُبَسَّط",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // --- Simulated Generating Stream Ambient Ribbon ---
        com.example.presentation.screens.dashboard.SimulatedVideoPlayerCanvas(
            isPlaying = !isReloading,
            animationType = "wave_motion",
            speedMultiplier = 1.0f,
            particleDensity = 14,
            accentColor = BrandTiffany,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )

        // --- Main Web Layout (Sidebar + Body Content) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(webBg)
        ) {
            if (isReloading) {
                // Loading simulation overlay
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.75f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = BrandTiffany)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "جاري الاتصال بقاعدة بيانات ملقم الويب السحابية...",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Web Responsive Left/Right Sidebar (shown when showSidebar is true)
                    if (showSidebar) {
                        Surface(
                            modifier = Modifier
                                .width(65.dp)
                                .fillMaxHeight(),
                            color = Color(0xFF07211D),
                            border = BorderStroke(0.dp, Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Home Icon Link
                                WebSidebarIconLink(
                                    icon = Icons.Default.Home,
                                    title = "الرئيسية",
                                    isActive = currentUrl == "/home",
                                    onClick = { if (currentUrl != "/home") navController.navigate(Screen.Home.route) }
                                )
                                // Chat icon
                                WebSidebarIconLink(
                                    icon = Icons.Default.Chat,
                                    title = "المحادثة",
                                    isActive = currentUrl.startsWith("/chat"),
                                    onClick = { if (!currentUrl.startsWith("/chat")) navController.navigate(Screen.Chat.createRoute("saas-direct-chat")) }
                                )
                                // Tools Icon
                                WebSidebarIconLink(
                                    icon = Icons.Default.Build,
                                    title = "الأدوات",
                                    isActive = currentUrl == "/tools",
                                    onClick = { if (currentUrl != "/tools") navController.navigate(Screen.Tools.route) }
                                )
                                // Subscriptions Icon
                                WebSidebarIconLink(
                                    icon = Icons.Default.CardMembership,
                                    title = "الاشتراكات",
                                    isActive = currentUrl == "/subscriptions",
                                    onClick = { if (currentUrl != "/subscriptions") navController.navigate(Screen.Subscriptions.route) }
                                )
                                // History Icon
                                WebSidebarIconLink(
                                    icon = Icons.Default.Menu,
                                    title = "السجل",
                                    isActive = currentUrl == "/history",
                                    onClick = { if (currentUrl != "/history") navController.navigate(Screen.History.route) }
                                )
                                // Dashboard / Gate
                                WebSidebarIconLink(
                                    icon = Icons.Default.Dashboard,
                                    title = "بوابة الويب",
                                    isActive = currentUrl == "/dashboard",
                                    onClick = { if (currentUrl != "/dashboard") navController.navigate(Screen.Dashboard.route) }
                                )
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                // Settings & profile
                                WebSidebarIconLink(
                                    icon = Icons.Default.Person,
                                    title = "الحساب",
                                    isActive = currentUrl == "/profile",
                                    onClick = { if (currentUrl != "/profile") navController.navigate(Screen.Profile.route) }
                                )
                                WebSidebarIconLink(
                                    icon = Icons.Default.Settings,
                                    title = "الإعدادات",
                                    isActive = currentUrl == "/settings",
                                    onClick = { if (currentUrl != "/settings") navController.navigate(Screen.Settings.route) }
                                )
                            }
                        }
                    }

                    // The actual screen content goes inside this Box container!
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun WebSidebarIconLink(
    icon: ImageVector,
    title: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) BrandTiffany.copy(alpha = 0.2f) else Color.Transparent,
        modifier = Modifier.size(46.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) BrandTiffany else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 7.sp,
                color = if (isActive) BrandTiffany else Color.White.copy(alpha = 0.5f),
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
