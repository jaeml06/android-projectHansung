package com.example.hansungmarket.control.main.fragment.sale.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hansungmarket.data.repo.AuthRepo
import com.example.hansungmarket.data.repo.ChatRepo
import com.example.hansungmarket.data.repo.SaleRepo
import com.example.hansungmarket.control.main.fragment.sale.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaleDetailViewModel : ViewModel() {
    private val _uis = MutableStateFlow(SaleDetailUiState(curUserId = requireNotNull(AuthRepo.curUserId)))
    val uis = _uis.asStateFlow()
    fun bind(postUuId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val ret = SaleRepo.getSaleDetail(postUuId)
            if (ret.isSuccess) {
                _uis.update {
                    it.copy(
                        selectI = ret.getOrNull()!!.toUiState(),
                        canBuy = ret.getOrNull()!!.toUiState().canSale
                    )
                }
            } else {
                _uis.update {
                    it.copy(
                        errorMessage = ret.exceptionOrNull()!!.localizedMessage,
                        loading = false
                    )
                }
            }
        }
    }

    fun canSaleEdit(uuid: String, currentSelectedSaleItemPossible: Boolean) {
        _uis.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result: Result<Unit> =
                SaleRepo.editSaleOnlyPossible(uuid, !currentSelectedSaleItemPossible)
            if (result.isSuccess) {
                _uis.update {
                    it.copy(loading = false, canBuy = !currentSelectedSaleItemPossible)
                }
            } else {
                _uis.update {
                    it.copy(errorMessage = "실패!", loading = false)
                }
            }
        }
    }
    fun delSelectP(uiState: SaleDetailUiState) {
        viewModelScope.launch {
            check(true)
            val result = SaleRepo.deleteSale(uiState.selectI!!.id)
            _uis.update {
                it.copy(
                    errorMessage = if (result.isSuccess) {
                        "게시물이 삭제되었습니다."
                    } else {
                        "실패!"
                    }, delEnd = true
                )
            }
        }
    }

    fun chat() {
        val postId = uis.value.selectI!!.id
        val sellerId = uis.value.selectI!!.sId
        viewModelScope.launch {
            check(true)
            val result = ChatRepo.createChattingRoom(postId = postId, sellerId = sellerId)
            _uis.update {
                it.copy(errorMessage = if (result.isSuccess) { "채팅방 생성" } else { "실패" })
            }
        }
    }
    fun errorMessageShown() {
        _uis.update { it.copy(errorMessage = null) }
    }
}
