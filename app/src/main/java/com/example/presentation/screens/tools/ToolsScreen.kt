package com.example.presentation.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.presentation.components.BottomNavBar
import com.example.presentation.navigation.Screen
import com.example.ui.theme.BrandTiffany

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var isWebSimulatorMode by remember { mutableStateOf(true) }

    val categories = listOf("الكل", "الإنشاء والكتابة", "الوسائط والفيديو", "الأتمتة والربط", "الاتصالات والصوت")

    val tools = listOf(
        AiToolItem(
            id = "text_generator",
            title = "مساعد الإنشاء الذكي",
            desc = "صناعة مقالات، ردود رسمية، ومحتوى تسويقي فورياً",
            category = "الإنشاء والكتابة",
            icon = Icons.Default.Create,
            tag = "رائج"
        ),
        AiToolItem(
            id = "video_ai",
            title = "منصة التوليد المرئي AI",
            desc = "صميم مقاطع سينمائية فائقة الجودة من نصوص ذكية",
            category = "الوسائط والفيديو",
            icon = Icons.Default.PlayArrow,
            tag = "بيتا"
        ),
        AiToolItem(
            id = "automations",
            title = "محرر الأتمتة والتدفقات",
            desc = "أتمتة العمليات والأحداث وربط متجرك بذكاء",
            category = "الأتمتة والربط",
            icon = Icons.Default.Settings,
            tag = "حديث"
        ),
        AiToolItem(
            id = "integrations",
            title = "مركز الربط والتكاملات",
            desc = "ربط فوري ومباشر مع Slack, WhatsApp, Zapier Webhook",
            category = "الأتمتة والربط",
            icon = Icons.Default.Share,
            tag = "نشط"
        ),
        AiToolItem(
            id = "call_center",
            title = "الكول سنتر والمساعد الصوتي",
            desc = "إدارة المكالمات الصوتية الآلية والتحويل الفوري",
            category = "الاتصالات والصوت",
            icon = Icons.Default.Call,
            tag = "ممتاز"
        )
    )

    val filteredTools = tools.filter {
        (selectedCategory == "الكل" || it.category == selectedCategory) &&
        (it.title.contains(searchQuery, ignoreCase = true) || it.desc.contains(searchQuery, ignoreCase = true))
    }

    val scaffoldContent = @Composable { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input Block
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث عن أي أداة أو تكامل...", fontSize = 14.sp) },
                prefix = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = BrandTiffany,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandTiffany,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            // Dynamic Category Filter Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) BrandTiffany.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isSelected) BrandTiffany else Color.Transparent, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandTiffany else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Grid Layout
            if (filteredTools.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "عذراً، لم نعثر على أي أدوات تطابق بحثك",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredTools) { tool ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.9f)
                                .clickable {
                                    navController.navigate(Screen.ToolDetail.createRoute(tool.id))
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Dynamic Badge / Tag
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BrandTiffany.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tool.tag,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTiffany
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(BrandTiffany.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tool.icon,
                                            contentDescription = null,
                                            tint = BrandTiffany,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = tool.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = tool.desc,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 14.sp
                                    )
                                }
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
            currentUrl = "/tools",
            pageTitle = "مركز الأدوات الذكية"
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("منظومة أدوات الذكاء الذكي", fontWeight = FontWeight.Bold) },
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
                    title = { Text("منظومة أدوات الذكاء الذكي", fontWeight = FontWeight.Bold) },
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

data class AiToolItem(
    val id: String,
    val title: String,
    val desc: String,
    val category: String,
    val icon: ImageVector,
    val tag: String
)
