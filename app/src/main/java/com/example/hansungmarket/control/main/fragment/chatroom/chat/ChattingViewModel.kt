package com.example.hansungmarket.control.main.fragment.chatroom.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.data.repo.ChatRepo
import com.example.hansungmarket.control.main.fragment.chatroom.room.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChattingViewModel : ViewModel() {
    private val _uis = MutableStateFlow(ChattingUiState())
    val uis = _uis.asStateFlow()
    private var contents = mutableListOf<ChatItemUiState>()
    fun updC(message: String){_uis.update{it.copy(content = message)}}
    fun bind(roomUuid: String) {
        _uis.update { it.copy(loading = true) }
        curRoomI(roomUuid)
        curRoomC(roomUuid)
        _uis.update { it.copy(loading = false) }
    }
    private fun curRoomI(roomUuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val result = ChatRepo.getChattingDetail(roomUuid)
            if (result.isSuccess)
                _uis.update { it.copy(curChatuis = result.getOrNull()!!.toUiState()) }
            else
                _uis.update { it.copy(errorMessage = result.exceptionOrNull()!!.localizedMessage) }
        }
    }
    private fun curRoomC(roomUuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val contentF = ChatRepo.getAllMessages(roomUuid)
            contentF.collect { lists ->
                if (lists.isNotEmpty()) {
                    if (contents.isEmpty()) {
                        contents = lists.map { chat -> chat.toUiState() }.toMutableList()
                        _uis.update { ui -> ui.copy(chats = contents) }
                    } else {
                        contents += lists.map { chat ->chat.toUiState() }.toMutableList()
                        _uis.update { ui -> ui.copy(chats = contents) }
                    }
                    _uis.update { ui ->
                        ui.copy(chats = lists.map { chat -> chat.toUiState() }.toMutableList())
                    }
                }
            }
        }
    }
    fun sendMessage() {
        val content = uis.value.content
        val roomId = uis.value.curChatuis!!.id
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val result = ChatRepo.sendMessage(roomUuid = roomId, message = content)
            if (result.isFailure) {
                _uis.update { it.copy(errorMessage = result.exceptionOrNull()!!.localizedMessage) }
            }
        }
    }
}