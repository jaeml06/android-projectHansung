package com.example.hansungmarket.control.main.fragment.profile

import androidx.paging.PagingData
import com.example.hansungmarket.model.UserDetail
import com.example.hansungmarket.control.main.fragment.sale.SaleItemUiState

data class ProfileUiState(
    val posts: PagingData<SaleItemUiState> = PagingData.empty(),
    val det: UserDetail? = null,
    val land: Boolean = true,
    val errorMessage: String? = null,
)
