package com.example.hansungmarket.control.signIn


data class SignInUiState(val E: String = "", val P: String = "", val L: Boolean = false, val isLogin: Boolean = false, val errorMessage: String? = null) {
    val isInputValid: Boolean
        get() = canBeP && isEmailValid
    private val isEmailValid: Boolean
        get() {
            if (E.isEmpty()) return false
            return android.util.Patterns.EMAIL_ADDRESS.matcher(E).matches()
        }
    private val canBeP: Boolean
        get() = P.length >= 8
    val passE: Boolean
        get() = P.isNotEmpty() && !canBeP
    val emailE: Boolean
        get() = E.isNotEmpty() && !isEmailValid
}