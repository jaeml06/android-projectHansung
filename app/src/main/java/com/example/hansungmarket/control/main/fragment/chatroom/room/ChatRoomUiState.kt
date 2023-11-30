package com.example.hansungmarket.control.main.fragment.chatroom.room

import androidx.paging.PagingData
import com.example.hansungmarket.model.ChatRoom

data class ChatRoomUiState(
    val rooms: PagingData<ChatRoomItemUiState> = PagingData.empty(),
    val curUserId: String, val errorMessage: String? = null)

data class ChatRoomItemUiState(
    val id: String,
    val userName: String,
    val profileImg: String?
)

fun ChatRoom.toUiState() = ChatRoomItemUiState(id = id, userName = userName, profileImg = profileImg)