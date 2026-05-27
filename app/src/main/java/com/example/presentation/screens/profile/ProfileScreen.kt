package com.example.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.UserSession
import com.example.presentation.components.BottomNavBar
import com.example.presentation.navigation.Screen
import com.example.ui.theme.BrandTiffany

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var isWebSimulatorMode by remember { mutableStateOf(true) }

    val scaffoldContent = @Composable { padding: PaddingValues ->
        val scrollState = rememberScrollState()
        val displayName = if (UserSession.displayName.isNotBlank()) UserSession.displayName else "وصال ستور"
        val userEmail = if (UserSession.email.isNotBlank()) UserSession.email else "wasal.stor@gmail.com"
        val isManager = UserSession.userRole == "manager"
        
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = BrandTiffany.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = displayName.take(1),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandTiffany
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(displayName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(userEmail, fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Dynamic Role Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isManager) Color(0xFF07211D) else BrandTiffany.copy(alpha = 0.12f),
                modifier = Modifier.padding(2.dp)
            ) {
                Text(
                    text = if (isManager) "💼 مدير ومُشرف المنظومة" else "👤 عميل المنصة المستفيد",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isManager) Color.White else BrandTiffany,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
            
            if (UserSession.loginMethod == "google") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("آمن ومربوط بحساب Google الموحد 🟢", fontSize = 10.sp, color = Color(0xFF27AE60), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Screen.Subscriptions.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTiffany)
            ) {
                Text("تـرقية باقة العمل المخصصة", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.Settings.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F6F4))
            ) {
                Text("إعدادات القنوات والمزامنة ⚙️", fontSize = 15.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    UserSession.isLoggedIn = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("تسجيل الخروج الآمن 🔓", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (isWebSimulatorMode) {
        com.example.presentation.components.WebBrowserShell(
            navController = navController,
            currentUrl = "/profile",
            pageTitle = "إدارة الحساب"
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("حسابي الشخصي", fontWeight = FontWeight.Bold) },
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
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                    )
                },
                bottomBar = { BottomNavBar(navController) },
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                scaffoldContent(padding)
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("حسابي الشخصي", fontWeight = FontWeight.Bold) },
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = { BottomNavBar(navController) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            scaffoldContent(padding)
        }
    }
}
