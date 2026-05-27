package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.ChatRepository
import com.example.data.repository.GeminiAiServiceImpl
import com.example.presentation.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    // Intercept uncaught exceptions across all threads to print clean error traces to Logcat
    val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      Log.e("CRITICAL_BUG", "Uncaught exception on thread ${thread.name}:", throwable)
      oldHandler?.uncaughtException(thread, throwable)
    }

    try {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      
      val database = AppDatabase.getDatabase(applicationContext)
      val chatRepository = ChatRepository(database.conversationDao(), database.messageDao())
      val aiService = GeminiAiServiceImpl(applicationContext)

      setContent {
        MyApplicationTheme {
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
              Surface(
                  modifier = Modifier.fillMaxSize(),
                  color = MaterialTheme.colorScheme.background
              ) {
                  val navController = rememberNavController()
                  AppNavigation(
                      navController = navController,
                      chatRepository = chatRepository,
                      aiService = aiService
                  )
              }
          }
        }
      }
    } catch (e: Throwable) {
      Log.e("CRITICAL_BUG", "Exception in MainActivity.onCreate:", e)
      throw e
    }
  }
}
