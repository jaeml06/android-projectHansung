package com.example.hansungmarket.presenter.main.fragment.profile

import androidx.paging.PagingData
import com.example.hansungmarket.model.UserDetail
import com.example.hansungmarket.presenter.main.fragment.sale.SaleItemUiState

data class ProfileUiState(
    val salePosts: PagingData<SaleItemUiState> = PagingData.empty(),
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
