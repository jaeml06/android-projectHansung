package com.example.hansungmarket.control.signIn


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.data.repo.AuthRepo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val _uis = MutableStateFlow(SignInUiState())
    val uis = _uis.asStateFlow()
    val login: Boolean
        get() = AuthRepo.isLogined()
    fun updE(email: String) {
        _uis.update { it.copy(E = email) }
    }
    fun updP(password: String) {
        _uis.update { it.copy(P = password) }
    }
    fun login() {
        val E = uis.value.E
        val P = uis.value.P
        _uis.update { it.copy(L = true) }
        viewModelScope.launch {
            val result = AuthRepo.signIn(E, P)
            if (result.isSuccess) {
                _uis.update { it.copy(isLogin = true, L = false) }
            } else {
                _uis.update {
                    it.copy(errorMessage = result.exceptionOrNull()!!.localizedMessage, L = false)
                }
            }
        }
    }
    fun userInfoExists(hasUserInfoCallback: (Boolean) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            hasUserInfoCallback(false)
            return
        }
        AuthRepo.checkUser(uid) {
            hasUserInfoCallback(it)
        }
    }
    fun userMessageShown() {
        _uis.update { it.copy(errorMessage = null) }
    }
}
