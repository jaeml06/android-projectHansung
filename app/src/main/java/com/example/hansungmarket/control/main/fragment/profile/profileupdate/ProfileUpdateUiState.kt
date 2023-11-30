package com.example.hansungmarket.control.main.fragment.profile.profileupdate

import android.graphics.Bitmap

data class ProfileUpdateUiState(
    val name: String = "",
    val loading: Boolean = false,
    val sib: Bitmap? = null,
    val suc: Boolean = false,
    val isc: Boolean = false,
    val errorMessage: String? = null,
)