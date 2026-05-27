package com.example.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSession
import com.example.presentation.components.TabseetLogo
import com.example.ui.theme.BrandTiffany
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("manager") } // "manager" (المدير) or "customer" (العميل)
    var showGoogleChooser by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    if (showGoogleChooser) {
        AlertDialog(
            onDismissRequest = { showGoogleChooser = false },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGoogleChooser = false }) {
                    Text("إلغاء", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TabseetLogo(modifier = Modifier.width(50.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اختر حساب Google للمتابعة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF07211D)
                    )
                    Text(
                        text = "منظومة تسجيل الدخول الآمن لقوقل ومزامنة القواعد",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    val accounts = listOf(
                        Triple("wasal.stor@gmail.com", "وصال ستور", "مستخدم رئيسي (العميل/المدير)"),
                        Triple("admin.tabseet@gmail.com", "المدير التنفيذي لتبسيط", "إدارة الأنظمة كاملة"),
                        Triple("khaled.dev@gmail.com", "المهندس خالد", "حساب مطور السحابة")
                    )
                    
                    accounts.forEach { (gEmail, gName, gDesc) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGoogleChooser = false
                                    isLoading = true
                                    scope.launch {
                                        delay(1200) // Realistic authenticating delay
                                        UserSession.email = gEmail
                                        UserSession.displayName = gName
                                        UserSession.isLoggedIn = true
                                        UserSession.userRole = selectedRole
                                        UserSession.loginMethod = "google"
                                        isLoading = false
                                        onLoginSuccess()
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6F4)),
                            border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BrandTiffany.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gName.take(1),
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTiffany,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(gName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF07211D))
                                    Text(gEmail, fontSize = 10.sp, color = Color.Gray)
                                    Text(gDesc, fontSize = 8.sp, color = BrandTiffany, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TabseetLogo(modifier = Modifier.width(80.dp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Database status indicator
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BrandTiffany.copy(alpha = 0.08f))
                    .border(1.dp, BrandTiffany.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = BrandTiffany,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "مزامنة القواعد السحابية والمصداقية قوقل نشطة 🟢",
                    fontSize = 10.sp,
                    color = BrandTiffany,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            Text(
                text = "تسجيل الدخول الموحد",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "أهلاً بك في المستقبل الرقمي مع منصة تبسيط الذكية",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Prominent Role Selection tabs ("المدير والعميل")
            Text(
                text = "اختر دورك للدخول والمصداقية:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F6F4), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Pair("manager", "💼 لوحة المدير / المالك"),
                    Pair("customer", "👤 بوابة العميل / المستخدم")
                ).forEach { (role, label) ->
                    val isSel = selectedRole == role
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) BrandTiffany else Color.Transparent)
                            .clickable { selectedRole = role }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Traditional Textfields
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("البريد الإلكتروني") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("كلمة المرور") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "نسيت كلمة المرور؟",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Traditional Submit Email Login
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            delay(1000)
                            UserSession.isLoggedIn = true
                            UserSession.userRole = selectedRole
                            UserSession.email = email
                            UserSession.displayName = if (selectedRole == "manager") "مكتشف لوحة تبسيط" else "عميل تبسيط العزيز"
                            UserSession.loginMethod = "email"
                            isLoading = false
                            onLoginSuccess()
                        }
                    } else {
                        // Fast login
                        UserSession.isLoggedIn = true
                        UserSession.userRole = selectedRole
                        UserSession.email = "demo@tabseet.ai"
                        UserSession.displayName = "ضيف تجريبي"
                        UserSession.loginMethod = "email"
                        onLoginSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF07211D))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (selectedRole == "manager") "دخول كمدير المنظومة 💼" else "دخول كعميل مستفيد 👤",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Google Login Button ("الدخول بقوقل")
            OutlinedButton(
                onClick = { showGoogleChooser = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2ECE9)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "جوجل",
                        tint = BrandTiffany,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الدخول السريع بحساب قوقل 🌐",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF07211D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("ليس لديك حساب؟ ", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                Text(
                    "إنشاء حساب جديد",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable(onClick = onNavigateToRegister)
                )
            }
        }
    }
}
