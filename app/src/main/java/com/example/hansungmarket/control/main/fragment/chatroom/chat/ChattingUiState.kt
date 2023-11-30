package com.example.hansungmarket.control.main.fragment.chatroom.chat

import com.example.hansungmarket.model.Chat
import com.example.hansungmarket.control.main.fragment.chatroom.room.ChatRoomItemUiState

data class ChattingUiState(
    val curChatuis: ChatRoomItemUiState? = null,
    val chats: MutableList<ChatItemUiState>? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val content: String = ""
)

data class ChatItemUiState(
    val id: String,
    val M: Boolean,
    val content: String,
    val timestamp: String,
    val profileImg: String?
)

fun Chat.toUiState() = ChatItemUiState(id = id, M = M, content = content, timestamp = timestamp, profileImg = profileImage)
