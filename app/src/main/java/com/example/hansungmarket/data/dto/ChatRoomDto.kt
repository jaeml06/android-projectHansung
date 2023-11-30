package com.example.hansungmarket.data.dto

import java.util.Date

data class ChatRoomDto(
    val chatRoomId: String = "",
    val postId: String = "",
    val writerUserId: String = "",
    val appliedUserId: String = "",
    val createTime: Date = Date()
)
