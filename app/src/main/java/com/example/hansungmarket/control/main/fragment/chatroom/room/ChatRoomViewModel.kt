package com.example.hansungmarket.control.main.fragment.chatroom.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.hansungmarket.control.main.fragment.chatroom.room.ChatRoomUiState
import com.example.hansungmarket.control.main.fragment.chatroom.room.toUiState
import com.example.hansungmarket.data.repo.AuthRepo
import com.example.hansungmarket.data.repo.ChatRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatRoomViewModel : ViewModel() {
    private val _uis = MutableStateFlow(ChatRoomUiState(curUserId = requireNotNull(AuthRepo.curUserId)))
    val uis = _uis.asStateFlow()
    fun bind() {
        viewModelScope.launch(Dispatchers.IO) {
            val pagingFlow = ChatRepo.getChatRoom()
            pagingFlow.cachedIn(viewModelScope).collect { pagingData ->
                _uis.update { state ->
                    state.copy(rooms = pagingData.map { it.toUiState() })
                }
            }
        }
    }
    fun ems() {
        _uis.update { it.copy(errorMessage = null) }
    }
}