package com.example.hansungmarket.model

data class Chat(
    val uuid: String,
    val isMain: Boolean,
    val message: String,
    val date: String,
    val profileImage: String?
)
