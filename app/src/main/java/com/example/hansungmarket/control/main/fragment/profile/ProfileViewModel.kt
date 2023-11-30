package com.example.hansungmarket.control.main.fragment.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.hansungmarket.data.repo.AuthRepo
import com.example.hansungmarket.data.repo.SaleRepo
import com.example.hansungmarket.data.repo.UserRepo
import com.example.hansungmarket.control.main.fragment.sale.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _uis = MutableStateFlow(ProfileUiState())
    val uis = _uis.asStateFlow()
    fun bind() {
        viewModelScope.launch {
            val ret = UserRepo.getUserDetail(requireNotNull(AuthRepo.curUserId))
            if (ret.isSuccess)
                _uis.update { it.copy(det = ret.getOrNull()!!, land = false) }
            val fl = SaleRepo.getMyFeeds()
            fl.cachedIn(viewModelScope).collect { pagingData ->
                _uis.update { uiState -> uiState.copy(posts = pagingData.map { it.toUiState() }) }
            }
        }
    }
    fun errorMessageShown() {
        _uis.update {
            it.copy(errorMessage = null)
        }
    }
}