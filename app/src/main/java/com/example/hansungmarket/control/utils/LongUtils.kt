package com.example.hansungmarket.control.utils

fun Long.toCostString(): String {
    return "${"%,d".format(this)}원"
}
