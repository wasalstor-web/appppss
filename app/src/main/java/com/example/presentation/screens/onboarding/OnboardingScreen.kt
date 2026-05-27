package com.example.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    
    val pages = listOf(
        OnboardingPage("ذكاء اصطناعي يفهم عملك", "اكتشف قوة الذكاء الاصطناعي في تحسين إنتاجيتك وإنجاز مهامك اليومية بسهولة واحترافية."),
        OnboardingPage("محادثات، أدوات، وذاكرة", "أدوات مخصصة لتلبية احتياجاتك، مع ذاكرة تحفظ محادثاتك لتعود إليها في أي وقت."),
        OnboardingPage("إنتاجية أسرع بواجهة عربية", "تجربة مصممة خصيصاً للمستخدم العربي لضمان تجربة سلسة واحترافية تليق بأعمالك.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Mock 3D abstract visual representation
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)))
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = pages[currentPage].title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pages[currentPage].description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Page Indicators
        Row(horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { index ->
                val color = if (index == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (index == currentPage) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    onFinishOnboarding()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = if (currentPage < pages.size - 1) "التالي" else "ابدأ الآن",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class OnboardingPage(val title: String, val description: String)
