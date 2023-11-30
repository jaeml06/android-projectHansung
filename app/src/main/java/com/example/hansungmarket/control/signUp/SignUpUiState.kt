package com.example.hansungmarket.control.signUp

data class SignUpUiState(
    val E: String = "", val P: String = "", val cP: String = "",
    val loading: Boolean = false, val S: Boolean = false, val errorMessage: String? = null) {
    val valid: Boolean
        get() = isEmailValid && isPasswordValid && isConfirmPasswordValid
    private val isEmailValid: Boolean
        get() {
            return if (E.isEmpty()) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(E).matches()
            }
        }
    private val isPasswordValid: Boolean
        get() = P.length >= 8
    private val isConfirmPasswordValid: Boolean
        get() = cP == P
    val showEmailError: Boolean
        get() = E.isNotEmpty() && !isEmailValid
    val showPasswordError: Boolean
        get() = P.isNotEmpty() && !isPasswordValid
    val showConfirmPasswordError: Boolean
        get() = cP.isNotEmpty() && !isConfirmPasswordValid
}
