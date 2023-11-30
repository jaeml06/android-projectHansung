package com.example.hansungmarket.control.main.fragment.sale

import androidx.paging.PagingData
import com.example.hansungmarket.model.Sale
import com.example.hansungmarket.model.ST
import com.example.hansungmarket.control.utils.toCostString

data class SaleUiState(
    val saleP: PagingData<SaleItemUiState> = PagingData.empty(),
    val errorMessage: String? = null,
    val sort: ST = ST.L,
    val flg: Boolean = false
)

data class SaleItemUiState(
    val id: String,
    val T: String,
    val sId: String,
    val sName: String,
    val sImgUrl: String?,
    val content: String,
    val imgUrl: String,
    val M: Boolean,
    val timestamp: String,
    val C: String,
    val canSale: Boolean
) : java.io.Serializable

fun Sale.toUiState() = SaleItemUiState(
    id = id,
    T = title,
    sId = sellerId,
    sName = sellerName,
    sImgUrl = sellerProfileImgUrl,
    content = content,
    imgUrl = imageUrl,
    M = M,
    C = price.toCostString(),
    timestamp = postTime,
    canSale = available
)
