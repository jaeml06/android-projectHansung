package com.example.hansungmarket.data.dto

data class UserDto(
    val userId: String = "",
    val name: String = "",
    val email: String? = null,
    val password: String? = null,
    val profileImgUrl: String? = null
)
