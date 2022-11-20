package com.adriane.chatapp.ui

import com.adriane.chatapp.Chat


data class MainState(
    val isLoggedIn: Boolean?,
    val error: Exception?,
    val chatList: List<Chat>,
)
