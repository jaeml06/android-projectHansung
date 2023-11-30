package com.example.hansungmarket.control.main.fragment.profile.profileupdate

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.data.repo.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileUpdateViewModel : ViewModel() {
    private val _uis = MutableStateFlow(ProfileUpdateUiState())
    val uis = _uis.asStateFlow()
    private var B = false
    private lateinit var prv: String
    private val isC
        get() = uis.value.isc || prv != uis.value.name
    val saved: Boolean
        get() = uis.value.name.isNotEmpty() && !uis.value.loading && isC
    fun bind(prv: String) {
        check(!B)
        B = true
        this.prv = prv
        _uis.update { it.copy(name = prv) }
    }
    fun updN(str: String) {
        _uis.update { it.copy(name = str) }
    }
    fun updI(b: Bitmap?) {
        _uis.update { it.copy(sib = b, isc = true) }
    }
    fun sC() {
        if (!isC) {
            _uis.update { it.copy(suc = true) }
            return
        }
        _uis.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val uiStateValue = uis.value
            val result = UserRepo.updateInfo(
                name = uiStateValue.name,
                profileImage = uiStateValue.sib,
                isImgChange = uiStateValue.isc
            )
            if (result.isSuccess) {
                _uis.update {
                    it.copy(suc = true, loading = false)
                }
            } else {
                _uis.update {
                    it.copy(
                        errorMessage = result.exceptionOrNull()!!.message,
                        loading = false
                    )
                }
            }
        }
    }
    fun errorMessageShown() {
        _uis.update { it.copy(errorMessage = null) }
    }
}