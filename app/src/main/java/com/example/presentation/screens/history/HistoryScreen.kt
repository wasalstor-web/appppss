package com.example.presentation.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.repository.ChatRepository
import com.example.domain.model.Conversation
import com.example.presentation.components.BottomNavBar
import com.example.presentation.navigation.Screen
import com.example.ui.theme.BrandTiffany

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, chatRepository: ChatRepository) {
    val conversations by chatRepository.getAllConversations().collectAsState(initial = emptyList())
    var isWebSimulatorMode by remember { mutableStateOf(true) }

    val scaffoldContent = @Composable { padding: PaddingValues ->
        if (conversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("لا توجد محادثات سابقة.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(conversations) { conv ->
                    ConversationItem(conv = conv, onClick = {
                        navController.navigate(Screen.Chat.createRoute(conv.id))
                    })
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                }
            }
        }
    }

    if (isWebSimulatorMode) {
        com.example.presentation.components.WebBrowserShell(
            navController = navController,
            currentUrl = "/history",
            pageTitle = "سجل المشاريع والمحادثات"
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("سجل المحادثات والسيناريوهات", fontWeight = FontWeight.Bold) },
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
                    title = { Text("سجل المحادثات والسيناريوهات", fontWeight = FontWeight.Bold) },
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

@Composable
fun ConversationItem(conv: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = conv.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            // Format timestamp in a real app. For now just show string.
            Text(text = "آخر تحديث: ${conv.updatedAt}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}
