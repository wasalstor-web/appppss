package com.example.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.BrandTiffany

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkThemeEnabled by remember { mutableStateOf(true) }
    var memoryEnabled by remember { mutableStateOf(true) }
    var isWebSimulatorMode by remember { mutableStateOf(true) }

    val scaffoldContent = @Composable { padding: PaddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            SettingSwitchItem(title = "تفعيل التنبيهات", checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
            Spacer(modifier = Modifier.height(16.dp))
            SettingSwitchItem(title = "المظهر الداكن", checked = darkThemeEnabled, onCheckedChange = { darkThemeEnabled = it })
            Spacer(modifier = Modifier.height(16.dp))
            SettingSwitchItem(title = "ذاكرة المحادثة المستمرة", checked = memoryEnabled, onCheckedChange = { memoryEnabled = it })
            
            Spacer(modifier = Modifier.weight(1f))
            Text("إصدار 1.0.0", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (isWebSimulatorMode) {
        com.example.presentation.components.WebBrowserShell(
            navController = navController,
            currentUrl = "/settings",
            pageTitle = "الإعدادات العامة"
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("إعدادات المنصة", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
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
                    title = { Text("إعدادات المنصة", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
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

@Composable
fun SettingSwitchItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
