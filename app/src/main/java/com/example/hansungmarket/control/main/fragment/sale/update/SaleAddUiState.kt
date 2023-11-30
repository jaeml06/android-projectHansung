package com.example.hansungmarket.control.main.fragment.sale.update

import android.net.Uri
import androidx.annotation.StringRes

data class SaleAddUiState(
    val T: String = "",
    val C: String = "",
    val CC: String = "",
    val S: Uri? = null,
    @StringRes val errorMessage: Int? = null,
    val isC: Boolean = true,
    val loading: Boolean = false,
    val SU: Boolean = false
) {
    val isInputValid: Boolean
        get() = isTitleValid && isContentValid && isCostValid && isImageValid
    private val isTitleValid: Boolean
        get() = T.isNotBlank()
    private val isContentValid: Boolean
        get() = C.isNotBlank()
    private val isCostValid: Boolean
        get() = CC.isNotBlank() && CC.matches(Regex("-?\\d+"))
    val showTitleError: Boolean
        get() = T.isNotEmpty() && !isTitleValid
    val showContentError: Boolean
        get() = C.isNotEmpty() && !isContentValid
    val showCostError: Boolean
        get() = CC.isNotEmpty() && !isCostValid
    private val isImageValid: Boolean
        get() = S != null
}