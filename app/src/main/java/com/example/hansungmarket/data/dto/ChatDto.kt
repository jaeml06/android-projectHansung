package com.example.hansungmarket.data.dto

import java.util.Date

data class ChatDto(
    val chatId: String = "",
    val userId: String = "",
    val content: String = "",
    val timeStamp: Date = Date()
)