package com.example.hansungmarket.presenter.common

fun Long.toCostString(): String {
    val str = "%,d".format(this)
    return "${str}원"
}