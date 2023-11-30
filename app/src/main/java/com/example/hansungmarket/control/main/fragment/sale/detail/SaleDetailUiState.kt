package com.example.hansungmarket.control.main.fragment.sale.detail

import com.example.hansungmarket.control.main.fragment.sale.SaleItemUiState

data class SaleDetailUiState(
    val selectI: SaleItemUiState? = null,
    val curUserId: String,
    val canBuy: Boolean? = null,
    val errorMessage: String? = null,
    val delEnd: Boolean = false,
    val loading: Boolean = false
)