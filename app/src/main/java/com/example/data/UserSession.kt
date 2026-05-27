package com.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserSession {
    var isLoggedIn by mutableStateOf(false)
    var userRole by mutableStateOf("manager") // "manager" (المدير) or "customer" (العميل)
    var email by mutableStateOf("")
    var displayName by mutableStateOf("")
    var avatarUrl by mutableStateOf("")
    var loginMethod by mutableStateOf("") // "google" or "email"
}
