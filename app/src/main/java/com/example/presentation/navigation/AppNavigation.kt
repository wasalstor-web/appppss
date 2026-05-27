package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.repository.ChatRepository
import com.example.domain.repository.AiService
import com.example.presentation.screens.splash.SplashScreen
import com.example.presentation.screens.onboarding.OnboardingScreen
import com.example.presentation.screens.auth.LoginScreen
import com.example.presentation.screens.auth.RegisterScreen
import com.example.presentation.screens.home.HomeScreen
import com.example.presentation.screens.history.HistoryScreen
import com.example.presentation.screens.tools.ToolsScreen
import com.example.presentation.screens.tools.ToolDetailScreen
import com.example.presentation.screens.profile.ProfileScreen
import com.example.presentation.screens.chat.ChatScreen
import com.example.presentation.screens.dashboard.DashboardScreen

import com.example.presentation.screens.settings.SettingsScreen
import com.example.presentation.screens.subscriptions.SubscriptionsScreen
import com.example.presentation.screens.notifications.NotificationsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    chatRepository: ChatRepository,
    aiService: AiService
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onNavigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                }
            )
        }
        
        composable(Screen.Chat.route) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(
                conversationId = conversationId,
                navController = navController,
                chatRepository = chatRepository,
                aiService = aiService
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(navController = navController, chatRepository = chatRepository)
        }

        composable(Screen.Tools.route) {
            ToolsScreen(navController = navController)
        }

        composable(Screen.ToolDetail.route) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
            ToolDetailScreen(
                toolId = toolId,
                navController = navController,
                aiService = aiService
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.Subscriptions.route) {
            SubscriptionsScreen(navController = navController)
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
    }
}
