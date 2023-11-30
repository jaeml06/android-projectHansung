package com.example.hansungmarket.data.dto

import java.util.Date

data class SaleDto(
    val id: String = "",
    val title: String = "",
    val price: Long = 0L,
    val content: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val postTime: Date = Date(),
    val available: Boolean = true
)
