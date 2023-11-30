package com.example.hansungmarket.model

data class UserDetail(
    val id: String,
    val name: String,
    val email: String?,
    val profileImgUrl: String?
) : java.io.Serializable
