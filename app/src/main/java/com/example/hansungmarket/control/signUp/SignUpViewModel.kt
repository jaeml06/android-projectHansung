package com.example.hansungmarket.control.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.data.repo.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _uis = MutableStateFlow(SignUpUiState())
    val uis = _uis.asStateFlow()
    fun updE(email: String) {
        _uis.update { it.copy(E = email) }
    }
    fun updP(password: String) {
        _uis.update { it.copy(P = password) }
    }
    fun updCP(confirmPassWord: String) {
        _uis.update { it.copy(cP = confirmPassWord) }
    }
    fun login() {
        val email = uis.value.E
        val password = uis.value.P
        _uis.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = AuthRepo.signUp(email, password)
            if (result.isSuccess) {
                _uis.update { it.copy(S = true, loading = false) }
            } else {
                _uis.update {
                    it.copy(
                        errorMessage = result.exceptionOrNull()!!.localizedMessage,
                        loading = false
                    )
                }
            }
        }
    }
    fun userMessageShown() {
        _uis.update { it.copy(errorMessage = null) }
    }
}