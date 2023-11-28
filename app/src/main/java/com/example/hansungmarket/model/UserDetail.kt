package com.example.hansungmarket.model

data class UserDetail(
    val uuid: String,
    val name: String,
    val email: String?,
    val profileImageUrl: String?
) : java.io.Serializable