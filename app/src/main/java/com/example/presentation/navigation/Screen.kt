package com.example.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
    object History : Screen("history")
    object Tools : Screen("tools")
    object ToolDetail : Screen("tool_detail/{toolId}") {
        fun createRoute(toolId: String) = "tool_detail/$toolId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Subscriptions : Screen("subscriptions")
    object Notifications : Screen("notifications")
    object Dashboard : Screen("dashboard")
}
