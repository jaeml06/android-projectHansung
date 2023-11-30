package com.example.hansungmarket.control.main.fragment.sale.update

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.R
import com.example.hansungmarket.data.repo.SaleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaleAddViewModel : ViewModel() {
    private val _uis = MutableStateFlow(SaleAddUiState())
    val uis = _uis.asStateFlow()
    fun updT(title: String) {
        _uis.update { it.copy(T = title) }
    }
    fun updC(content: String) {
        _uis.update { it.copy(C = content) }
    }
    fun updCC(cost: String) {
        _uis.update { it.copy(CC = cost) }
    }
    fun S(selectedImage: Uri) {
        _uis.update { it.copy(S = selectedImage) }
    }
    fun toE() {
        _uis.update { it.copy(isC = false) }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun upS() {
        _uis.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = SaleRepo.uploadSale(
                title = uis.value.T,
                content = uis.value.C,
                imgUrl = uis.value.S!!,
                cost = uis.value.CC
            )
            if (result.isSuccess) {
                _uis.update { it.copy(SU = true, loading = false) }
            } else {
                _uis.update {
                    it.copy(
                        errorMessage = R.string.F, loading = false
                    )
                }
            }
        }
    }
    fun eC(uuid: String) {
        _uis.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = SaleRepo.editSale(
                uuid = uuid,
                title = uis.value.T,
                content = uis.value.C,
                imageUri = uis.value.S!!,
                cost = uis.value.CC
            )
            if (result.isSuccess) {
                _uis.update { it.copy(SU = true, loading = false) }
            } else {
                _uis.update {
                    it.copy(errorMessage = R.string.F, loading = false)
                }
            }
        }
    }
}