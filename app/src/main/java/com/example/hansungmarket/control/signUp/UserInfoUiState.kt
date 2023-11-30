package com.example.hansungmarket.control.signUp

sealed class UserInfoUiState {
    object N : UserInfoUiState()
    object Loading : UserInfoUiState()
    object E : UserInfoUiState()
    data class F(val exception: Throwable) :UserInfoUiState()
}
