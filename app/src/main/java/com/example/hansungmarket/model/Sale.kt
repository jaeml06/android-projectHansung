package com.example.hansungmarket.model

data class Sale(
    val id: String,
    val title: String,
    val sellerId: String,
    val sellerName: String,
    val sellerProfileImgUrl: String?,
    val content: String,
    val imageUrl: String,
    val M: Boolean,
    val postTime: String,
    val price: Long,
    val available: Boolean
)