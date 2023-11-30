package com.example.hansungmarket.control.signUp

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.data.repo.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserInfoViewModel : ViewModel() {
    private val _uis: MutableStateFlow<UserInfoUiState> = MutableStateFlow(UserInfoUiState.N)
    val uis = _uis.asStateFlow()
    var N: String = ""
    var S: Bitmap? = null
    fun sendInfo() {
        _uis.update { UserInfoUiState.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            val result = UserRepo.saveInitUserInfo(N, S)
            if (result.isSuccess) {
                _uis.update { UserInfoUiState.E }
            } else {
                _uis.update { UserInfoUiState.F(result.exceptionOrNull()!!) }
            }
        }
    }
}