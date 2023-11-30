package com.example.hansungmarket.control.main.fragment.sale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.hansungmarket.data.repo.SaleRepo
import com.example.hansungmarket.model.ST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaleViewModel : ViewModel() {
    private val _uis = MutableStateFlow(SaleUiState())
    val uis = _uis.asStateFlow()
    fun updateSortType(sortType: String) {
        println(sortType)
        when (sortType) {
            ST.L.T -> _uis.update { it.copy(sort = ST.L) }
            ST.E.T -> _uis.update { it.copy(sort = ST.E) }
            ST.C.T -> _uis.update { it.copy(sort = ST.C) }
        }
    }
    fun canUpd(flags: Boolean) {
        _uis.update { it.copy(flg = flags) }
    }
    fun bind() {
        viewModelScope.launch(Dispatchers.IO) {
            val fl = SaleRepo.getHomeFeeds(uis.value.sort, uis.value.flg)
            fl.cachedIn(viewModelScope).collect { pagingData ->
                _uis.update { uiState -> uiState.copy(saleP = pagingData.map { it.toUiState() }) }
            }
        }
    }
    fun errorMessageShown() {
        _uis.update { it.copy(errorMessage = null) }
    }
}