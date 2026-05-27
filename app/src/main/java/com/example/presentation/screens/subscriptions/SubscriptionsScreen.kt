package com.example.presentation.screens.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(navController: NavController) {
    var isWebSimulatorMode by remember { mutableStateOf(true) }
    val plans = listOf(
        Plan("الخطة المجانية", "٠ ر.س", listOf("١٠٠ رسالة شهرياً", "أدوات أساسية")),
        Plan("برو", "٤٩ ر.س", listOf("رسائل لامحدودة", "جميع الأدوات بذكاء عالي", "أولوية في الدعم")),
        Plan("الأعمال", "١٩٩ ر.س", listOf("وصول قوي للـ API", "تدريب الذكاء الاصطناعي الخاص", "دعم مخصص 24/7"))
    )

    val scaffoldContent = @Composable { padding: PaddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(plans) { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.price, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
                        Text("شهرياً", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        plan.features.forEach { feature ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(feature, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { /* TODO */ },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("اختر الباقة")
                        }
                    }
                }
            }
        }
    }

    if (isWebSimulatorMode) {
        com.example.presentation.components.WebBrowserShell(
            navController = navController,
            currentUrl = "/subscriptions",
            pageTitle = "الباقات والاشتراكات"
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("الباقات والاشتراكات", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                            }
                        },
                        actions = {
                            Button(
                                onClick = { isWebSimulatorMode = !isWebSimulatorMode },
                                colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BrandTiffany),
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
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                scaffoldContent(padding)
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("الباقات والاشتراكات", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
                    actions = {
                        Button(
                            onClick = { isWebSimulatorMode = !isWebSimulatorMode },
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BrandTiffany),
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
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            scaffoldContent(padding)
        }
    }
}

data class Plan(val title: String, val price: String, val features: List<String>)
